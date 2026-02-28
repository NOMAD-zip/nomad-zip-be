package com.nz.nomadzip.recommend.service;

import com.nz.nomadzip.recommend.config.RecommendationProperties;
import com.nz.nomadzip.recommend.dto.RecommendationFilters;
import com.nz.nomadzip.recommend.model.LodgingCandidateDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RuleEngineService {

    private final RecommendationProperties recommendationProperties;

    public double score(LodgingCandidateDocument lodging, RecommendationFilters filters, String query) {
        RecommendationProperties.RuleWeights weights = recommendationProperties.getRuleWeights();

        double priceScore = priceScore(lodging, filters);
        double safetyScore = normalize(lodging.getSafetyScore(), 0.5);
        double ratingScore = normalize(lodging.getAverageRating() == null ? null : lodging.getAverageRating() / 5.0, 0.5);
        double distanceScore = distanceScore(lodging, filters);
        double baseRuleScore;

        double weightSum = weights.getPrice() + weights.getSafety() + weights.getDistance() + weights.getRating();
        if (weightSum <= 0) {
            baseRuleScore = 0.0;
        } else {
            double weighted = (priceScore * weights.getPrice())
                    + (safetyScore * weights.getSafety())
                    + (distanceScore * weights.getDistance())
                    + (ratingScore * weights.getRating());

            baseRuleScore = clamp01(weighted / weightSum);
        }

        double intentScore = intentScore(query, lodging);
        return clamp01((baseRuleScore * 0.70) + (intentScore * 0.30));
    }

    private double intentScore(String query, LodgingCandidateDocument lodging) {
        String normalizedQuery = normalizeText(query);
        if (normalizedQuery.isBlank()) {
            return 0.5;
        }

        boolean oceanWanted = containsAny(normalizedQuery,
                List.of("바다", "해변", "비치", "오션", "ocean", "beach", "coast"));
        boolean workWanted = containsAny(normalizedQuery,
                List.of("개발자", "일하기", "업무", "워크", "워케이션", "remote", "work", "코워킹", "노트북"));

        if (!oceanWanted && !workWanted) {
            return 0.5;
        }

        List<Double> components = new ArrayList<>();
        String lodgingText = normalizeText(String.join(" ",
                safe(lodging.getLodgingName()),
                safe(lodging.getSummary()),
                safe(lodging.getDescription()),
                safe(lodging.getAddressBasic()),
                safe(lodging.getAddressDetail()),
                safe(lodging.getTagText()),
                safe(lodging.getAmenityText())));

        if (oceanWanted) {
            double oceanSignal = containsAny(lodgingText,
                    List.of("바다", "해변", "비치", "오션", "ocean", "beach", "coast", "seaview", "오션뷰"))
                    ? 1.0 : 0.0;
            components.add(oceanSignal);
        }

        if (workWanted) {
            double wifiSignal = Boolean.TRUE.equals(lodging.getWifi()) ? 1.0 : 0.0;
            double workKeywordSignal = containsAny(lodgingText,
                    List.of("와이파이", "wifi", "책상", "업무", "work", "remote", "코워킹", "co-working", "조용", "집중"))
                    ? 1.0 : 0.0;
            components.add((wifiSignal * 0.6) + (workKeywordSignal * 0.4));
        }

        if (components.isEmpty()) {
            return 0.5;
        }

        return clamp01(components.stream().mapToDouble(Double::doubleValue).average().orElse(0.5));
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^0-9a-zA-Z가-힣\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean containsAny(String source, List<String> keywords) {
        for (String keyword : keywords) {
            if (source.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }

    private double priceScore(LodgingCandidateDocument lodging, RecommendationFilters filters) {
        if (lodging.getMonthlyPrice() == null) {
            return 0.5;
        }
        if (filters == null || filters.getPriceMax() == null || filters.getPriceMax() <= 0) {
            return 0.5;
        }

        double ratio = (double) lodging.getMonthlyPrice() / filters.getPriceMax();
        if (ratio <= 1.0) {
            return clamp01(1.0 - (ratio * 0.6));
        }
        return Math.max(0.0, 1.0 - ratio);
    }

    private double distanceScore(LodgingCandidateDocument lodging, RecommendationFilters filters) {
        if (filters == null
                || filters.getLatitude() == null
                || filters.getLongitude() == null
                || filters.getMaxDistanceKm() == null
                || filters.getMaxDistanceKm() <= 0
                || lodging.getLatitude() == null
                || lodging.getLongitude() == null) {
            return 0.5;
        }

        double distanceKm = haversine(filters.getLatitude(), filters.getLongitude(),
                lodging.getLatitude(), lodging.getLongitude());
        if (distanceKm > filters.getMaxDistanceKm()) {
            return 0.0;
        }
        return clamp01(1.0 - (distanceKm / filters.getMaxDistanceKm()));
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double sinLat = Math.sin(dLat / 2);
        double sinLon = Math.sin(dLon / 2);
        double a = sinLat * sinLat
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * sinLon * sinLon;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private double normalize(Double value, double fallback) {
        if (value == null) {
            return fallback;
        }
        return clamp01(value);
    }

    private double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
