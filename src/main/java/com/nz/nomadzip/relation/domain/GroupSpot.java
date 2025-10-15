package com.nz.nomadzip.relation.domain;

import com.nz.nomadzip.group.command.domain.entity.Group;
import com.nz.nomadzip.spot.command.domain.entity.Spot;
import com.nz.nomadzip.user.command.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_spot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GroupSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    private Spot spot;
}
