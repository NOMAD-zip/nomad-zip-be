package com.nz.nomadzip.recommend.controller;

import com.nz.nomadzip.common.dto.ApiResponse;
import com.nz.nomadzip.recommend.dto.AccuracyReportResponse;
import com.nz.nomadzip.recommend.dto.RecommendationRequest;
import com.nz.nomadzip.recommend.dto.RecommendationResponse;
import com.nz.nomadzip.recommend.service.RecommendationAccuracyService;
import com.nz.nomadzip.recommend.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationAccuracyService recommendationAccuracyService;

    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<RecommendationResponse>> recommend(
            @Valid @RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(recommendationService.recommend(request)));
    }

    @GetMapping("/recommend/accuracy/offline")
    public ResponseEntity<ApiResponse<AccuracyReportResponse>> evaluateOffline() {
        return ResponseEntity.ok(ApiResponse.success(recommendationAccuracyService.evaluateOffline()));
    }
}
