package com.nz.nomadzip.review.query.mapper;

import com.nz.nomadzip.review.query.dto.response.ReviewResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewQueryMapper {
    List<ReviewResponse> findByLodgingId(@Param("lodgingId") Long lodgingId);
}