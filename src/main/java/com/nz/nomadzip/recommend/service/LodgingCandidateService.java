package com.nz.nomadzip.recommend.service;

import com.nz.nomadzip.recommend.config.RecommendationProperties;
import com.nz.nomadzip.recommend.dto.RecommendationFilters;
import com.nz.nomadzip.recommend.mapper.RecommendationQueryMapper;
import com.nz.nomadzip.recommend.model.LodgingCandidateDocument;
import com.nz.nomadzip.recommend.model.VectorCandidate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LodgingCandidateService {

    private final RecommendationQueryMapper recommendationQueryMapper;
    private final RecommendationProperties recommendationProperties;

    public List<VectorBackedLodging> loadCandidates(List<VectorCandidate> vectorCandidates,
                                                    RecommendationFilters filters) {
        if (vectorCandidates == null || vectorCandidates.isEmpty()) {
            List<LodgingCandidateDocument> fallback = recommendationQueryMapper.findFallbackCandidates(
                    filters == null ? null : filters.getPriceMax(),
                    filters != null ? filters.getWifi() : null,
                    recommendationProperties.getTopK());

            List<VectorBackedLodging> fallbackCandidates = new ArrayList<>();
            for (LodgingCandidateDocument lodging : fallback) {
                if (passesHardFilters(lodging, filters)) {
                    fallbackCandidates.add(new VectorBackedLodging(lodging, 0.0));
                }
            }
            return fallbackCandidates;
        }

        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        for (VectorCandidate candidate : vectorCandidates) {
            ids.add(candidate.lodgingId());
        }

        List<LodgingCandidateDocument> lodgings = recommendationQueryMapper.findByLodgingIds(new ArrayList<>(ids));
        Map<Long, LodgingCandidateDocument> lodgingMap = new LinkedHashMap<>();
        for (LodgingCandidateDocument lodging : lodgings) {
            lodgingMap.put(lodging.getLodgingId(), lodging);
        }

        List<VectorBackedLodging> combined = new ArrayList<>();
        for (VectorCandidate vectorCandidate : vectorCandidates) {
            LodgingCandidateDocument lodging = lodgingMap.get(vectorCandidate.lodgingId());
            if (lodging == null) {
                continue;
            }
            if (!passesHardFilters(lodging, filters)) {
                continue;
            }
            combined.add(new VectorBackedLodging(lodging, vectorCandidate.semanticScore()));
        }
        return combined;
    }

    private boolean passesHardFilters(LodgingCandidateDocument lodging, RecommendationFilters filters) {
        if (filters == null) {
            return true;
        }

        if (filters.getPriceMax() != null
                && lodging.getMonthlyPrice() != null
                && lodging.getMonthlyPrice() > filters.getPriceMax()) {
            return false;
        }

        if (Boolean.TRUE.equals(filters.getWifi()) && !Boolean.TRUE.equals(lodging.getWifi())) {
            return false;
        }

        if (filters.getMinSafetyScore() != null
                && lodging.getSafetyScore() != null
                && lodging.getSafetyScore() < filters.getMinSafetyScore()) {
            return false;
        }

        if (filters.getMinAverageRating() != null
                && lodging.getAverageRating() != null
                && lodging.getAverageRating() < filters.getMinAverageRating()) {
            return false;
        }

        return true;
    }

    public record VectorBackedLodging(LodgingCandidateDocument lodging, double semanticScore) {
    }
}
