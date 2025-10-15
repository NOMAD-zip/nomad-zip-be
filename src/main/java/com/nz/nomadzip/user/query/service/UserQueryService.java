package com.nz.nomadzip.user.query.service;

import com.nz.nomadzip.common.dto.StatusType;
import com.nz.nomadzip.common.exception.BusinessException;
import com.nz.nomadzip.user.command.domain.entity.User;
import com.nz.nomadzip.user.exception.UserErrorCode;
import com.nz.nomadzip.user.query.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {
    private final UserQueryRepository userQueryRepository;
    public User findValidUser(Long userId){
        return userQueryRepository.findByUserIdAndIsDeleted(userId, StatusType.N)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}
