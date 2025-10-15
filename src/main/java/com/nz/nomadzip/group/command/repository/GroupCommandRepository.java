package com.nz.nomadzip.group.command.repository;

import com.nz.nomadzip.group.command.domain.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupCommandRepository extends JpaRepository<Group, Long> {
}
