package com.nz.nomadzip.recommend.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationResponse(
        String originalQuery,
        String refinedQuery,
        int totalCandidates,
        List<String> queryKeywords,
        List<RecommendationItem> recommendations
) {
}
