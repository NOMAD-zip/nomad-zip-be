package com.nz.nomadzip.group.command.domain.entity;

import com.nz.nomadzip.spot.command.domain.entity.Spot;
import com.nz.nomadzip.user.command.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "group")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;
    /*
        유저 - 그룹, 관광지 - 그룹
     다대다 관계는 중간에 테이블 놓아서 해결하기!
    */

    private String title;

    private String info;

    private Date startedAt;

    private Date endedAt;

    private Integer maxParticipants;

    private Integer currentParticipants;
}
