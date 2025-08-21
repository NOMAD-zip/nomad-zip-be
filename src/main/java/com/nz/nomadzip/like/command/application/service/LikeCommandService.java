package com.nz.nomadzip.like.command.application.service;

import com.nz.nomadzip.like.command.application.dto.request.LikeRequest;

public interface LikeCommandService {
    void registerLike(LikeRequest request);
    void cancelLike(LikeRequest request);
}