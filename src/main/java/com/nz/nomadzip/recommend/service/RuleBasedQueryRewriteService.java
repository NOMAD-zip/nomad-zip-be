package com.nz.nomadzip.recommend.service;

import com.nz.nomadzip.recommend.dto.RecommendationFilters;
import com.nz.nomadzip.recommend.model.QueryRewriteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RuleBasedQueryRewriteService {

    private final QueryTextNormalizer queryTextNormalizer;

    public QueryRewriteResult rewrite(String query, RecommendationFilters filters) {
        Set<String> keywordSet = new LinkedHashSet<>(queryTextNormalizer.tokens(query));

        if (filters != null) {
            if (Boolean.TRUE.equals(filters.getWifi())) {
                keywordSet.add("와이파이");
            }
            if (filters.getPriceMax() != null) {
                keywordSet.add("예산");
            }
            if (filters.getMinSafetyScore() != null) {
                keywordSet.add("안전");
            }
            if (filters.getMinAverageRating() != null) {
                keywordSet.add("평점");
            }
            if (filters.getLatitude() != null && filters.getLongitude() != null) {
                keywordSet.add("거리");
            }
        }

        List<String> keywords = new ArrayList<>(keywordSet);
        String refined = String.join(" ", keywords);
        if (refined.isBlank()) {
            refined = query == null ? "숙소 추천" : query;
        }

        return QueryRewriteResult.fallback(refined, keywords);
    }
}
