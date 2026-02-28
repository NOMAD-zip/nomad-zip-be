package com.nz.nomadzip.recommend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RelevanceScoringService {

    private final ManualLabelDatasetService manualLabelDatasetService;
    private final QueryTextNormalizer queryTextNormalizer;

    public int score(String query, Long lodgingId) {
        if (lodgingId == null) {
            return 0;
        }

        String normalized = queryTextNormalizer.normalize(query);
        Map<Long, Integer> direct = manualLabelDatasetService.getLabelsByQuery().get(normalized);
        if (direct != null) {
            return direct.getOrDefault(lodgingId, 0);
        }

        String nearestQuery = findNearestQuery(normalized);
        if (nearestQuery == null) {
            return 0;
        }

        return manualLabelDatasetService.getLabelsByQuery()
                .getOrDefault(nearestQuery, Map.of())
                .getOrDefault(lodgingId, 0);
    }

    public Map<String, Map<Long, Integer>> labelsByQuery() {
        return manualLabelDatasetService.getLabelsByQuery();
    }

    public int pairCount() {
        return manualLabelDatasetService.getRows().size();
    }

    public int queryCount() {
        return manualLabelDatasetService.getLabelsByQuery().size();
    }

    private String findNearestQuery(String normalizedQuery) {
        List<String> queryKeys = manualLabelDatasetService.getLabelsByQuery().keySet().stream().toList();
        if (queryKeys.isEmpty()) {
            return null;
        }

        Set<String> targetTokens = new HashSet<>(queryTextNormalizer.tokens(normalizedQuery));
        double bestScore = 0.0;
        String bestQuery = null;

        for (String candidate : queryKeys) {
            Set<String> candidateTokens = new HashSet<>(queryTextNormalizer.tokens(candidate));
            double similarity = jaccard(targetTokens, candidateTokens);
            if (similarity > bestScore) {
                bestScore = similarity;
                bestQuery = candidate;
            }
        }

        return bestScore >= 0.30 ? bestQuery : null;
    }

    private double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);

        Set<String> union = new HashSet<>(a);
        union.addAll(b);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
}
