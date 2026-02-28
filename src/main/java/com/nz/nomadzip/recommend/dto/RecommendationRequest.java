package com.nz.nomadzip.recommend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationRequest {

    @NotBlank
    private String query;

    @Valid
    private RecommendationFilters filters = new RecommendationFilters();

    private Integer topN;
}
