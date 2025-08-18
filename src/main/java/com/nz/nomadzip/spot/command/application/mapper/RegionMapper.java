package com.nz.nomadzip.spot.command.application.mapper;

import com.nz.nomadzip.client.tourapi.dto.RegionResponse;
import com.nz.nomadzip.spot.command.domain.entity.Region;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring", // 스프링 빈으로 주입!
        unmappedSourcePolicy = ReportingPolicy.ERROR    // 매핑이 안된게 있으면 컴파일 에러 발생시키기!
)
public interface RegionMapper {

    @Mapping(target = "regionId", expression = "java(toLong(item.code()))")    // PK 임의 지정
    @Mapping(target = "sigunguCode", ignore = true) //
    @Mapping(source = "item.code", target = "areaCode", qualifiedByName = "toLong")
    @Mapping(source = "item.name", target = "regionName")
    @BeanMapping(ignoreUnmappedSourceProperties = {"rnum"}) // rnum 무시
    Region toAreaEntity(RegionResponse.Item item);

    @Mapping(target = "regionId", expression = "java(makePk(areaCode, item.code()))")    // PK 임의 지정
    @Mapping(source = "areaCode", target = "areaCode")
    @Mapping(source = "item.code", target = "sigunguCode", qualifiedByName = "toLong")
    @Mapping(source = "item.name", target = "regionName")
    @BeanMapping(ignoreUnmappedSourceProperties = {"rnum"}) // rnum 무시
    Region toSigunguEntity(RegionResponse.Item item, Long areaCode);


    @Named("toLong")
    default Long toLong(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Long.parseLong(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    default Long makePk(Long areaCode, String sigunguCode) {
        if (areaCode == null || sigunguCode == null) return null;
        /* 시/도 코드 + 시군구 코드 해서 PK 만들기! */
        return Long.parseLong(areaCode.toString() + sigunguCode);
    }
}
