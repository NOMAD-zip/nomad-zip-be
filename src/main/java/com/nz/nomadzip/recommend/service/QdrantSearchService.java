package com.nz.nomadzip.recommend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nz.nomadzip.recommend.config.RecommendationProperties;
import com.nz.nomadzip.recommend.dto.RecommendationFilters;
import com.nz.nomadzip.recommend.model.VectorCandidate;
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
public class QdrantSearchService {

    @Qualifier("qdrantWebClient")
    private final WebClient qdrantWebClient;
    private final EmbeddingService embeddingService;
    private final RecommendationProperties recommendationProperties;

    public List<VectorCandidate> search(String refinedQuery, RecommendationFilters filters, int limit) {
        try {
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("vector", embeddingService.embedQuery(refinedQuery));
            requestBody.put("limit", limit);
            requestBody.put("with_payload", true);
            requestBody.put("with_vector", false);

            Map<String, Object> filterBody = buildFilter(filters);
            if (!filterBody.isEmpty()) {
                requestBody.put("filter", filterBody);
            }

            QdrantSearchResponse response = qdrantWebClient.post()
                    .uri("/collections/{collection}/points/search", recommendationProperties.getQdrant().getCollection())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(QdrantSearchResponse.class)
                    .timeout(Duration.ofMillis(recommendationProperties.getQdrant().getTimeoutMs()))
                    .block();

            if (response == null || response.result == null) {
                return List.of();
            }

            List<VectorCandidate> candidates = new ArrayList<>();
            for (QdrantPoint point : response.result) {
                Long lodgingId = extractLodgingId(point);
                if (lodgingId == null) {
                    continue;
                }
                candidates.add(new VectorCandidate(lodgingId, normalizeScore(point.score)));
            }
            return candidates;
        } catch (Exception ex) {
            log.warn("Qdrant search failed. fallback to rule-based retrieval.", ex);
            return List.of();
        }
    }

    private Map<String, Object> buildFilter(RecommendationFilters filters) {
        if (filters == null) {
            return Map.of();
        }

        List<Map<String, Object>> must = new ArrayList<>();
        if (filters.getPriceMax() != null) {
            must.add(Map.of(
                    "key", "monthly_price",
                    "range", Map.of("lte", filters.getPriceMax())
            ));
        }

        if (Boolean.TRUE.equals(filters.getWifi())) {
            must.add(Map.of(
                    "key", "wifi",
                    "match", Map.of("value", true)
            ));
        }

        if (must.isEmpty()) {
            return Map.of();
        }
        return Map.of("must", must);
    }

    private Long extractLodgingId(QdrantPoint point) {
        if (point == null) {
            return null;
        }

        Long byPointId = toLong(point.id);
        if (byPointId != null) {
            return byPointId;
        }

        if (point.payload == null) {
            return null;
        }

        Long byPayload = toLong(point.payload.get("lodging_id"));
        if (byPayload != null) {
            return byPayload;
        }

        return toLong(point.payload.get("lodgingId"));
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private double normalizeScore(Double score) {
        if (score == null) {
            return 0.0;
        }
        if (score < 0) {
            return Math.max(0.0, (score + 1.0) / 2.0);
        }
        if (score <= 1.0) {
            return score;
        }
        return Math.min(1.0, score / 2.0);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class QdrantSearchResponse {
        @JsonProperty("result")
        private List<QdrantPoint> result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class QdrantPoint {
        @JsonProperty("id")
        private Object id;

        @JsonProperty("score")
        private Double score;

        @JsonProperty("payload")
        private Map<String, Object> payload;
    }
}
