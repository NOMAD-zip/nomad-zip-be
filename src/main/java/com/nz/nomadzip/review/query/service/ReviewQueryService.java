package com.nz.nomadzip.review.query.service;

import com.nz.nomadzip.review.query.dto.response.ReviewResponse;
import java.util.List;

public interface ReviewQueryService {
    List<ReviewResponse> getReviewsByLodgingId(Long lodgingId);
}