package com.nz.nomadzip.recommend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LodgingCandidateDocument {

    private Long lodgingId;
    private String lodgingName;
    private Long monthlyPrice;
    private Integer capacity;
    private Double latitude;
    private Double longitude;
    private String addressBasic;
    private String addressDetail;
    private Integer reviewTotal;
    private String summary;
    private String description;
    private Double averageRating;
    private String rule;
    private Boolean wifi;
    private Double safetyScore;
    private String amenityText;
    private String tagText;
}
