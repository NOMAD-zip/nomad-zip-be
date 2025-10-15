package com.nz.nomadzip.relation.repository;

import com.nz.nomadzip.group.command.domain.entity.Group;
import com.nz.nomadzip.relation.domain.GroupUser;
import com.nz.nomadzip.relation.domain.RoleType;
import com.nz.nomadzip.user.command.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    boolean existsByGroupAndRoleType(Group group, RoleType roleType);

    boolean existsByGroupAndUser(Group group, User user);
}
