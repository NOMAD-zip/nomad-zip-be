package com.nz.nomadzip.review.query.controller;

import com.nz.nomadzip.common.dto.ApiResponse;
import com.nz.nomadzip.review.query.dto.response.ReviewResponse;
import com.nz.nomadzip.review.query.service.ReviewQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lodgings")
@RequiredArgsConstructor
public class ReviewQueryController {

    private final ReviewQueryService reviewQueryService;

    @GetMapping("/{lodgingId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> list(@PathVariable Long lodgingId) {
        return ResponseEntity.ok(ApiResponse.success(reviewQueryService.getReviewsByLodgingId(lodgingId)));
    }
}