package com.nz.nomadzip.recommend.service;

import com.nz.nomadzip.recommend.config.RecommendationProperties;
import com.nz.nomadzip.recommend.dto.RecommendationFilters;
import com.nz.nomadzip.recommend.dto.RecommendationItem;
import com.nz.nomadzip.recommend.dto.RecommendationRequest;
import com.nz.nomadzip.recommend.dto.RecommendationResponse;
import com.nz.nomadzip.recommend.model.LodgingCandidateDocument;
import com.nz.nomadzip.recommend.model.QueryRewriteResult;
import com.nz.nomadzip.recommend.model.RankedCandidate;
import com.nz.nomadzip.recommend.model.VectorCandidate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationProperties recommendationProperties;
    private final QueryRewriteService queryRewriteService;
    private final RuleBasedQueryRewriteService ruleBasedQueryRewriteService;
    private final QdrantSearchService qdrantSearchService;
    private final LodgingCandidateService lodgingCandidateService;
    private final RelevanceScoringService relevanceScoringService;
    private final RerankerService rerankerService;
    private final RuleEngineService ruleEngineService;

    public RecommendationResponse recommend(RecommendationRequest request) {
        RecommendationFilters filters = request.getFilters() == null
                ? new RecommendationFilters()
                : request.getFilters();

        QueryRewriteResult rewritten = rewriteWithFailSafe(request.getQuery(), filters);

        List<VectorCandidate> vectorCandidates = qdrantSearchService.search(
                rewritten.refinedQuery(),
                filters,
                recommendationProperties.getTopK());

        List<LodgingCandidateService.VectorBackedLodging> candidates = lodgingCandidateService.loadCandidates(vectorCandidates, filters);
        List<LodgingCandidateDocument> candidateDocs = candidates.stream()
                .map(LodgingCandidateService.VectorBackedLodging::lodging)
                .toList();
        Map<Long, Double> rerankerScores = rerankerService.score(rewritten.refinedQuery(), candidateDocs);

        RecommendationProperties.Weights weights = recommendationProperties.getWeights();
        double weightSum = weights.getSemantic() + weights.getRelevance() + weights.getRule();

        List<RankedCandidate> rankedCandidates = candidates.stream()
                .map(candidate -> {
                    double semanticScore = candidate.semanticScore();
                    double relevanceScore = rerankerScores.getOrDefault(
                            candidate.lodging().getLodgingId(),
                            relevanceScoringService.score(rewritten.refinedQuery(), candidate.lodging().getLodgingId()) / 3.0);
                    double ruleScore = ruleEngineService.score(candidate.lodging(), filters, rewritten.refinedQuery());
                    double finalScore = weightSum == 0
                            ? 0.0
                            : ((semanticScore * weights.getSemantic())
                            + (relevanceScore * weights.getRelevance())
                            + (ruleScore * weights.getRule())) / weightSum;

                    return new RankedCandidate(candidate.lodging(), semanticScore, relevanceScore, ruleScore, finalScore);
                })
                .sorted(Comparator.comparingDouble(RankedCandidate::finalScore).reversed())
                .toList();

        int topN = request.getTopN() == null ? recommendationProperties.getTopN() : request.getTopN();

        List<RecommendationItem> items = rankedCandidates.stream()
                .limit(topN)
                .map(ranked -> RecommendationItem.builder()
                        .id(ranked.lodging().getLodgingId())
                        .name(ranked.lodging().getLodgingName())
                        .location(formatLocation(ranked.lodging().getAddressBasic(), ranked.lodging().getAddressDetail()))
                        .price(ranked.lodging().getMonthlyPrice())
                        .score(round(ranked.finalScore()))
                        .semanticScore(round(ranked.semanticScore()))
                        .relevanceScore(round(ranked.relevanceScore()))
                        .ruleScore(round(ranked.ruleScore()))
                        .safetyScore(round(defaultIfNull(ranked.lodging().getSafetyScore(), 0.5)))
                        .averageRating(round(defaultIfNull(ranked.lodging().getAverageRating(), 0.0)))
                        .build())
                .toList();

        return RecommendationResponse.builder()
                .originalQuery(request.getQuery())
                .refinedQuery(rewritten.refinedQuery())
                .totalCandidates(rankedCandidates.size())
                .queryKeywords(rewritten.keywords())
                .recommendations(items)
                .build();
    }

    private QueryRewriteResult rewriteWithFailSafe(String query, RecommendationFilters filters) {
        try {
            return queryRewriteService.rewriteAsync(query, filters)
                    .get(recommendationProperties.getQueryTimeoutMs(), TimeUnit.MILLISECONDS);
        } catch (Exception ignored) {
            return ruleBasedQueryRewriteService.rewrite(query, filters);
        }
    }

    private String formatLocation(String basic, String detail) {
        if (detail == null || detail.isBlank()) {
            return basic;
        }
        return basic + " " + detail;
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private double defaultIfNull(Double value, double fallback) {
        return value == null ? fallback : value;
    }
}
