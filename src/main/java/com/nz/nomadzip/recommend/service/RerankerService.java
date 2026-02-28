package com.nz.nomadzip.recommend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nz.nomadzip.recommend.config.RecommendationProperties;
import com.nz.nomadzip.recommend.model.LodgingCandidateDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RerankerService {

    @Qualifier("rerankerWebClient")
    private final WebClient rerankerWebClient;
    private final RecommendationProperties recommendationProperties;

    public Map<Long, Double> score(String query, List<LodgingCandidateDocument> candidates) {
        if (!recommendationProperties.getReranker().isEnabled() || candidates == null || candidates.isEmpty()) {
            return Map.of();
        }

        try {
            RerankRequest request = new RerankRequest();
            request.query = query;
            request.candidates = new ArrayList<>();

            for (LodgingCandidateDocument candidate : candidates) {
                RerankCandidate rerankCandidate = new RerankCandidate();
                rerankCandidate.lodgingId = candidate.getLodgingId();
                rerankCandidate.text = buildLodgingText(candidate);
                request.candidates.add(rerankCandidate);
            }

            RerankResponse response = rerankerWebClient.post()
                    .uri("/rerank")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(RerankResponse.class)
                    .timeout(Duration.ofMillis(recommendationProperties.getReranker().getTimeoutMs()))
                    .block();

            if (response == null || response.scores == null) {
                return Map.of();
            }

            Map<Long, Double> scoreByLodging = new LinkedHashMap<>();
            for (RerankScore score : response.scores) {
                if (score == null || score.lodgingId == null || score.score == null) {
                    continue;
                }
                scoreByLodging.put(score.lodgingId, clamp01(score.score));
            }
            return scoreByLodging;
        } catch (Exception ex) {
            log.warn("External reranker call failed. fallback to label-based relevance scoring.", ex);
            return Map.of();
        }
    }

    private String buildLodgingText(LodgingCandidateDocument candidate) {
        StringBuilder builder = new StringBuilder();
        append(builder, candidate.getLodgingName());
        append(builder, candidate.getSummary());
        append(builder, candidate.getDescription());
        append(builder, candidate.getAddressBasic());
        append(builder, candidate.getAddressDetail());
        append(builder, candidate.getRule());
        return builder.toString().trim();
    }

    private void append(StringBuilder builder, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(value);
    }

    private double clamp01(Double value) {
        if (value == null) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RerankRequest {
        @JsonProperty("query")
        public String query;

        @JsonProperty("candidates")
        public List<RerankCandidate> candidates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RerankCandidate {
        @JsonProperty("lodging_id")
        public Long lodgingId;

        @JsonProperty("text")
        public String text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RerankResponse {
        @JsonProperty("scores")
        public List<RerankScore> scores;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RerankScore {
        @JsonProperty("lodging_id")
        public Long lodgingId;

        @JsonProperty("score")
        public Double score;
    }
}
