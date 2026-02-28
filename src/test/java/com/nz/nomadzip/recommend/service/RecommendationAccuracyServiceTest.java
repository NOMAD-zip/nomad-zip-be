package com.nz.nomadzip.recommend.service;

import com.nz.nomadzip.recommend.config.RecommendationProperties;
import com.nz.nomadzip.recommend.dto.AccuracyReportResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationAccuracyServiceTest {

    @Test
    void rerankedModelShouldOutperformBaselineOffline() {
        RecommendationProperties properties = new RecommendationProperties();
        QueryTextNormalizer normalizer = new QueryTextNormalizer();
        ManualLabelDatasetService datasetService = new ManualLabelDatasetService(
                properties,
                normalizer,
                new DefaultResourceLoader());
        datasetService.load();

        RelevanceScoringService relevanceScoringService = new RelevanceScoringService(datasetService, normalizer);
        RecommendationAccuracyService accuracyService = new RecommendationAccuracyService(relevanceScoringService, properties);

        AccuracyReportResponse report = accuracyService.evaluateOffline();

        assertEquals(1200, report.pairCount());
        assertTrue(report.rerankedNdcgAt10() >= report.baselineNdcgAt10());
        assertTrue(report.rerankedPrecisionAt5() >= report.baselinePrecisionAt5());
    }
}
