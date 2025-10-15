package com.nz.nomadzip.user.query.repository;


import com.nz.nomadzip.common.dto.StatusType;
import com.nz.nomadzip.user.command.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserQueryRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserIdAndIsDeleted(Long userId, StatusType status);
}
