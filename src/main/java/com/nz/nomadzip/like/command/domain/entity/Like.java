package com.nz.nomadzip.like.command.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id")
    private Long likesId;
    @Column(name = "lodging_id")
    private Long lodgingId;
    @Column(name = "user_id")
    private Long userId;

    public Like(Long lodgingId, Long userId) {
        this.lodgingId = lodgingId;
        this.userId = userId;
    }
}
