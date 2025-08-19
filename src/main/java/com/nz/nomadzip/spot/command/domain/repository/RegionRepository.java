package com.nz.nomadzip.spot.command.domain.repository;

import com.nz.nomadzip.spot.command.domain.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {

}
