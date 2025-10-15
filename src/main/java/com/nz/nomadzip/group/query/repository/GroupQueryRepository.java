package com.nz.nomadzip.group.query.repository;

import com.nz.nomadzip.group.command.domain.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupQueryRepository extends JpaRepository<Group, Long> {
}
