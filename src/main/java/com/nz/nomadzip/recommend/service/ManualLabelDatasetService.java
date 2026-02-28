package com.nz.nomadzip.recommend.service;

import com.nz.nomadzip.recommend.config.RecommendationProperties;
import com.nz.nomadzip.recommend.model.ManualLabelRow;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManualLabelDatasetService {

    private static final int REQUIRED_PAIR_SIZE = 1200;

    private final RecommendationProperties recommendationProperties;
    private final QueryTextNormalizer queryTextNormalizer;
    private final ResourceLoader resourceLoader;

    @Getter
    private List<ManualLabelRow> rows = List.of();

    @Getter
    private Map<String, Map<Long, Integer>> labelsByQuery = Map.of();

    @PostConstruct
    public void load() {
        String path = "classpath:" + recommendationProperties.getDataset().getManualLabelPath();
        Resource resource = resourceLoader.getResource(path);

        List<ManualLabelRow> loadedRows = new ArrayList<>();
        Map<String, Map<Long, Integer>> labelMap = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 4) {
                    continue;
                }

                long pairId = Long.parseLong(parts[0].trim());
                String query = parts[1].trim();
                long lodgingId = Long.parseLong(parts[2].trim());
                int relevance = Integer.parseInt(parts[3].trim());

                ManualLabelRow row = new ManualLabelRow(pairId, query, lodgingId, relevance);
                loadedRows.add(row);

                String normalizedQuery = queryTextNormalizer.normalize(query);
                labelMap.computeIfAbsent(normalizedQuery, key -> new LinkedHashMap<>())
                        .put(lodgingId, relevance);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("수작업 라벨 데이터 로딩 실패: " + path, ex);
        }

        if (loadedRows.size() != REQUIRED_PAIR_SIZE) {
            log.warn("수작업 라벨 데이터 권장 개수(1200)와 다릅니다. 현재: {}", loadedRows.size());
        }

        this.rows = List.copyOf(loadedRows);
        this.labelsByQuery = Map.copyOf(labelMap);
    }
}
