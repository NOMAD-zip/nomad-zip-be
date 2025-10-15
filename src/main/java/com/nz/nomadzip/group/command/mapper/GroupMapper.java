package com.nz.nomadzip.group.command.mapper;

import com.nz.nomadzip.group.command.domain.entity.Group;
import com.nz.nomadzip.relation.domain.GroupUser;
import com.nz.nomadzip.relation.domain.RoleType;
import com.nz.nomadzip.spot.command.application.dto.request.CreateGroupRequest;
import com.nz.nomadzip.user.command.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {RoleType.class})
public interface GroupMapper {

    /* 그룹 엔티티 변환 */
    @Mapping(target = "id", ignore = true)
    Group toEntity(CreateGroupRequest dto);

}
