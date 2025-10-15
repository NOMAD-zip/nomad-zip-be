package com.nz.nomadzip.relation.service;


import com.nz.nomadzip.common.exception.BusinessException;
import com.nz.nomadzip.common.exception.ErrorCode;
import com.nz.nomadzip.group.command.domain.entity.Group;
import com.nz.nomadzip.group.exception.GroupErrorCode;
import com.nz.nomadzip.relation.domain.GroupUser;
import com.nz.nomadzip.relation.domain.RoleType;
import com.nz.nomadzip.relation.repository.GroupUserRepository;
import com.nz.nomadzip.user.command.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupUserService {
    private final GroupUserRepository groupUserRepository;

    /* 반드시 기존 트랙잭션 (그룹 생성에 참여해야함!) */
    @Transactional(propagation = Propagation.MANDATORY)
    public void createLeader(Group group, User user){
        /* 리더 존재 확인*/
        boolean isLeaderExists = groupUserRepository.existsByGroupAndRoleType(group, RoleType.LEADER);

        /* 중복 멤버 확인 */
        boolean alreadyMember = groupUserRepository.existsByGroupAndUser(group, user);

        if(alreadyMember){
            throw new BusinessException(GroupErrorCode.ALREADY_JOIN);
        }

        GroupUser leaderUser = GroupUser.builder()
                .group(group)
                .user(user)
                .roleType(RoleType.LEADER)
                .build();

        groupUserRepository.save(leaderUser);
    }
}
