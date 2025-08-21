package com.nz.nomadzip.like.command.application.service;

import com.nz.nomadzip.like.command.application.dto.request.LikeRequest;
import com.nz.nomadzip.like.command.domain.entity.Like;
import com.nz.nomadzip.like.command.domain.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeCommandServiceImpl implements LikeCommandService {

    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public void registerLike(LikeRequest request) {
        likeRepository.findByLodgingIdAndUserId(request.lodgingId(), request.userId())
                .orElseGet(() -> likeRepository.save(request.toEntity()));
    }


    @Override
    @Transactional
    public void cancelLike(LikeRequest request) {
        likeRepository.deleteByLodgingIdAndUserId(request.lodgingId(), request.userId());
    }
}
