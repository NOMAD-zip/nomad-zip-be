package com.nz.nomadzip.recommend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationFilters {

    private Long priceMax;
    private Boolean wifi;
    private Double minSafetyScore;
    private Double minAverageRating;
    private Double latitude;
    private Double longitude;
    private Double maxDistanceKm;
}
