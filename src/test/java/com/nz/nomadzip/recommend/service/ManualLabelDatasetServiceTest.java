package com.nz.nomadzip.recommend.service;

import com.nz.nomadzip.recommend.config.RecommendationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManualLabelDatasetServiceTest {

    @Test
    void manualLabelDatasetMustContainExactly1200Pairs() {
        RecommendationProperties properties = new RecommendationProperties();
        ManualLabelDatasetService service = new ManualLabelDatasetService(
                properties,
                new QueryTextNormalizer(),
                new DefaultResourceLoader());

        service.load();

        assertEquals(1200, service.getRows().size());
        assertEquals(30, service.getLabelsByQuery().size());
    }
}
