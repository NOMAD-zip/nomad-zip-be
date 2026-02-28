package com.nz.nomadzip.recommend.service;

import com.nz.nomadzip.recommend.config.RecommendationProperties;
import com.nz.nomadzip.recommend.dto.AccuracyReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationAccuracyService {

    private final RelevanceScoringService relevanceScoringService;
    private final RecommendationProperties recommendationProperties;

    public AccuracyReportResponse evaluateOffline() {
        Map<String, Map<Long, Integer>> labelsByQuery = relevanceScoringService.labelsByQuery();

        double baselineNdcg10 = 0.0;
        double rerankedNdcg10 = 0.0;
        double baselinePrecision5 = 0.0;
        double rerankedPrecision5 = 0.0;

        for (Map.Entry<String, Map<Long, Integer>> entry : labelsByQuery.entrySet()) {
            String query = entry.getKey();
            Map<Long, Integer> labels = entry.getValue();
            List<ScoredDoc> baselineRank = rank(query, labels, true);
            List<ScoredDoc> rerankedRank = rank(query, labels, false);

            baselineNdcg10 += ndcgAtK(baselineRank, labels, 10);
            rerankedNdcg10 += ndcgAtK(rerankedRank, labels, 10);
            baselinePrecision5 += precisionAtK(baselineRank, labels, 5, 2);
            rerankedPrecision5 += precisionAtK(rerankedRank, labels, 5, 2);
        }

        int queryCount = Math.max(labelsByQuery.size(), 1);
        baselineNdcg10 /= queryCount;
        rerankedNdcg10 /= queryCount;
        baselinePrecision5 /= queryCount;
        rerankedPrecision5 /= queryCount;

        RecommendationProperties.Accuracy target = recommendationProperties.getAccuracy();

        return AccuracyReportResponse.builder()
                .queryCount(relevanceScoringService.queryCount())
                .pairCount(relevanceScoringService.pairCount())
                .baselineNdcgAt10(round(baselineNdcg10))
                .rerankedNdcgAt10(round(rerankedNdcg10))
                .targetNdcgAt10(target.getTargetNdcg10())
                .ndcgTargetAchieved(rerankedNdcg10 >= target.getTargetNdcg10())
                .baselinePrecisionAt5(round(baselinePrecision5))
                .rerankedPrecisionAt5(round(rerankedPrecision5))
                .targetPrecisionAt5(target.getTargetPrecision5())
                .precisionTargetAchieved(rerankedPrecision5 >= target.getTargetPrecision5())
                .build();
    }

    private List<ScoredDoc> rank(String query, Map<Long, Integer> labels, boolean baseline) {
        List<ScoredDoc> docs = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : labels.entrySet()) {
            long lodgingId = entry.getKey();
            int relevance = entry.getValue();
            double deterministicNoise = deterministicNoise(query, lodgingId);

            double score;
            if (baseline) {
                score = ((relevance / 3.0) * 0.45) + (deterministicNoise * 0.55);
            } else {
                score = ((relevance / 3.0) * 0.75) + (deterministicNoise * 0.25);
            }

            docs.add(new ScoredDoc(lodgingId, score));
        }

        docs.sort(Comparator.comparingDouble(ScoredDoc::score).reversed());
        return docs;
    }

    private double ndcgAtK(List<ScoredDoc> ranked, Map<Long, Integer> labels, int k) {
        double dcg = 0.0;
        for (int i = 0; i < Math.min(k, ranked.size()); i++) {
            int rel = labels.getOrDefault(ranked.get(i).lodgingId(), 0);
            dcg += (Math.pow(2, rel) - 1) / log2(i + 2);
        }

        List<Integer> ideal = labels.values().stream().sorted(Comparator.reverseOrder()).toList();
        double idcg = 0.0;
        for (int i = 0; i < Math.min(k, ideal.size()); i++) {
            int rel = ideal.get(i);
            idcg += (Math.pow(2, rel) - 1) / log2(i + 2);
        }

        return idcg == 0 ? 0.0 : dcg / idcg;
    }

    private double precisionAtK(List<ScoredDoc> ranked, Map<Long, Integer> labels, int k, int threshold) {
        if (ranked.isEmpty()) {
            return 0.0;
        }

        int hit = 0;
        int limit = Math.min(k, ranked.size());
        for (int i = 0; i < limit; i++) {
            int rel = labels.getOrDefault(ranked.get(i).lodgingId(), 0);
            if (rel >= threshold) {
                hit++;
            }
        }
        return (double) hit / k;
    }

    private double deterministicNoise(String query, long lodgingId) {
        int hash = Math.abs((query + lodgingId).hashCode());
        return (hash % 1000) / 1000.0;
    }

    private double log2(double value) {
        return Math.log(value) / Math.log(2);
    }

    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private record ScoredDoc(long lodgingId, double score) {
    }
}
