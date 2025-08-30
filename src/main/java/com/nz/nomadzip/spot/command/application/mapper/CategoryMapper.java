package com.nz.nomadzip.spot.command.application.mapper;


import com.nz.nomadzip.client.tourapi.dto.CategoryResponse;
import com.nz.nomadzip.spot.command.domain.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.ERROR    // 매핑 안된 요소 에러 발생!
//        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CategoryMapper {
    @Mapping(source = "item.lclsSystm1Cd", target = "categoryCode")
    @Mapping(source = "item.lclsSystm1Nm", target = "categoryName")
    @BeanMapping(ignoreUnmappedSourceProperties = {"lclsSystm2Cd", "lclsSystm2Nm", "lclsSystm3Cd", "lclsSystm3Nm", "rnum"})
    Category toDepth1CategoryEntity(CategoryResponse.Item item);

    @Mapping(source = "item.lclsSystm2Cd", target = "categoryCode")
    @Mapping(source = "item.lclsSystm2Nm", target = "categoryName")
    @BeanMapping(ignoreUnmappedSourceProperties = {"lclsSystm1Cd", "lclsSystm1Nm", "lclsSystm3Cd", "lclsSystm3Nm", "rnum"})
    Category toDepth2CategoryEntity(CategoryResponse.Item item);

    @BeanMapping(ignoreUnmappedSourceProperties = {"lclsSystm1Cd", "lclsSystm1Nm", "lclsSystm2Cd", "lclsSystm2Nm", "rnum"})
    @Mapping(source = "item.lclsSystm3Cd", target = "categoryCode")
    @Mapping(source = "item.lclsSystm3Nm", target = "categoryName")
    Category toDepth3CategoryEntity(CategoryResponse.Item item);
}
