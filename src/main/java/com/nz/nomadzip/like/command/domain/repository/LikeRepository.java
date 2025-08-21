package com.nz.nomadzip.like.command.domain.repository;

import com.nz.nomadzip.like.command.domain.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByLodgingIdAndUserId(Long lodgingId, Long userId);

    void deleteByLodgingIdAndUserId(Long lodgingId, Long userId);
}
