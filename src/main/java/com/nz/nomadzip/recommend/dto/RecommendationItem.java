package com.nz.nomadzip.recommend.dto;

import lombok.Builder;

@Builder
public record RecommendationItem(
        Long id,
        String name,
        String location,
        Long price,
        Double score,
        Double semanticScore,
        Double relevanceScore,
        Double ruleScore,
        Double safetyScore,
        Double averageRating
) {
}
