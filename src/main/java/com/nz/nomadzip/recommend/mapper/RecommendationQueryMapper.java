package com.nz.nomadzip.recommend.mapper;

import com.nz.nomadzip.recommend.model.LodgingCandidateDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecommendationQueryMapper {

    List<LodgingCandidateDocument> findByLodgingIds(@Param("lodgingIds") List<Long> lodgingIds);

    List<LodgingCandidateDocument> findFallbackCandidates(@Param("priceMax") Long priceMax,
                                                          @Param("wifiOnly") Boolean wifiOnly,
                                                          @Param("limit") int limit);
}
