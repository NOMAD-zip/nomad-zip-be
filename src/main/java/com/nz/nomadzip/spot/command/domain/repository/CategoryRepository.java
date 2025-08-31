package com.nz.nomadzip.spot.command.domain.repository;

import com.nz.nomadzip.spot.command.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryCode(String categoryCode);
}
