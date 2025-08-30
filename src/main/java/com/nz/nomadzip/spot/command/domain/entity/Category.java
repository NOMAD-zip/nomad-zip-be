package com.nz.nomadzip.spot.command.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="category")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;


    @Column(unique = true)
    private String categoryCode;

    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    Category parentCategory;

    // 부모 카테고리 명시적 작성을 위해 Lombok 미사용!
    public void setParentCategory(Category parentCategory){
        this.parentCategory = parentCategory;
    }
}
