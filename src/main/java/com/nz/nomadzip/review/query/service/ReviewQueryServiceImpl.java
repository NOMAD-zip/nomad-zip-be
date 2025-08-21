package com.nz.nomadzip.review.query.service;

import com.nz.nomadzip.review.query.dto.response.ReviewResponse;
import com.nz.nomadzip.review.query.mapper.ReviewQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewQueryServiceImpl implements ReviewQueryService {

    private final ReviewQueryMapper mapper;

    @Override
    @Transactional
    public List<ReviewResponse> getReviewsByLodgingId(Long lodgingId) {
        return mapper.findByLodgingId(lodgingId);
    }
}