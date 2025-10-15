package com.nz.nomadzip.group.command.application.service;

import com.nz.nomadzip.group.command.domain.entity.Group;
import com.nz.nomadzip.group.command.mapper.GroupMapper;
import com.nz.nomadzip.group.command.repository.GroupCommandRepository;
import com.nz.nomadzip.relation.service.GroupUserService;
import com.nz.nomadzip.spot.command.application.dto.request.CreateGroupRequest;
import com.nz.nomadzip.user.command.domain.entity.User;
import com.nz.nomadzip.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GroupCommandService {
    private final GroupCommandRepository groupCommandRepository;
    private final GroupMapper groupMapper;
    private final UserQueryService userQueryService;
    private final GroupUserService groupUserService;

    @Transactional
    public void createGroup(CreateGroupRequest request) {
        /* - 입력값 유효 로직은 DTO에서 처리! */

        /* 1. 그룹 생성 */
        Group group = groupMapper.toEntity(request);
        groupCommandRepository.save(group);

        /* 2. 사용자 검증 */
        /* TODO JWT 완성 시키면 생성자로 바꿔주기! */
        User leader = userQueryService.findValidUser(request.getUserId());

        /* 3. 리더 지정 및 저장 */
        groupUserService.createLeader(group, leader);
    }
}
