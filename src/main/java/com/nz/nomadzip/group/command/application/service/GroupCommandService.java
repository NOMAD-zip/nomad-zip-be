package com.nz.nomadzip.group.command.application.service;

import com.nz.nomadzip.spot.command.application.dto.request.CreateGroupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GroupCommandService {

    @Transactional
    public void createGroup(CreateGroupRequest createGroupRequest) {
        /* 1. 그룹 중복 확인
         * - 입력값 유효 로직은 DTO에서 처리! */



        /* 2. 그룹 생성 */
    }
}
