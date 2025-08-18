package com.nz.nomadzip.spot.command.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "region")
public class Region {

    @Id
    private Long regionId;

    private Long areaCode;

    private Long sigunguCode;

    private String regionName;
}
