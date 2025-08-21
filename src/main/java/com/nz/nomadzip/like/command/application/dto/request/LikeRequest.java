package com.nz.nomadzip.like.command.application.dto.request;

import com.nz.nomadzip.like.command.domain.entity.Like;
import lombok.Builder;

@Builder
public record LikeRequest(Long lodgingId, Long userId) {

    public Like toEntity() {
        return new Like(lodgingId, userId);
    }
}