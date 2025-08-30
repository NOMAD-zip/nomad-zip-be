package com.nz.nomadzip.spot.command.application.service;

import com.nz.nomadzip.client.tourapi.dto.CategoryResponse;
import com.nz.nomadzip.client.tourapi.dto.RegionResponse;
import com.nz.nomadzip.client.tourapi.port.TourApiPort;
import com.nz.nomadzip.common.exception.BusinessException;
import com.nz.nomadzip.spot.command.application.mapper.CategoryMapper;
import com.nz.nomadzip.spot.command.application.mapper.RegionMapper;
import com.nz.nomadzip.spot.command.domain.entity.Category;
import com.nz.nomadzip.spot.command.domain.entity.Region;
import com.nz.nomadzip.spot.command.domain.repository.CategoryRepository;
import com.nz.nomadzip.spot.command.domain.repository.RegionRepository;
import com.nz.nomadzip.spot.exception.SpotErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class SpotCommandService {
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;
    private final TourApiPort tourApi;
    private final RegionMapper regionMapper;
    private final CategoryMapper categoryMapper;
    private final WebClient webClient;

    @Transactional
    public void fetchRegion() {
        List<Region> regionList = new ArrayList<>();    // 최종 저장 시 사용할 지역 배열

        // 부모 지역 호출
        RegionResponse parentRegions = tourApi.getAreaCode(100);

        // 부모 지역 repository 저장하기

        for (RegionResponse.Item item : parentRegions.response().body().items().item()) {
            // 부모 지역 repository 저장하기
            regionList.add(regionMapper.toAreaEntity(item));

            RegionResponse childRegionResponse = tourApi.getAreaCode(item.code(), 100);

            for(RegionResponse.Item childRegion : childRegionResponse.response().body().items().item()){
                regionList.add(regionMapper.toSigunguEntity(childRegion, Long.parseLong(item.code())));
            }
        }

        regionRepository.saveAll(regionList);
    }

    @Transactional
    public void fetchCategory() {
        Map<String, Category> parentCategoryMap = new HashMap<>();
        Map<String, Category> childCategoryMap = new HashMap<>();

        /* 초기 설정 카테고리 목록*/
        final List<InitCategory> initCategoryList = List.of(
                new InitCategory("EV01", 2), // 축제
                new InitCategory("EX",   1), // 체험 관광
                new InitCategory("HS",   1), // 역사 관광
                new InitCategory("NA",   1), // 자연 관광
                new InitCategory("VE",   1)  // 문화 관광
        );
        // 1. 카테고리 api 호출
        for(InitCategory category: initCategoryList){
            CategoryResponse categoryCodes = tourApi.getCategory(category.categoryCode, category.depth);

            for(CategoryResponse.Item item : categoryCodes.response().body().items().item()){
                Category parentCategory = null;
                Category childCategory = null;
                // case 1: depth가 1인 경우
                if(category.depth == 1){
                    parentCategory = categoryMapper.toDepth1CategoryEntity(item);
                    childCategory = categoryMapper.toDepth2CategoryEntity(item);
                }

                // case 2: depth가 2인 경우
                if(category.depth == 2){
                    parentCategory = categoryMapper.toDepth2CategoryEntity(item);
                    childCategory = categoryMapper.toDepth3CategoryEntity(item);
                }

                if(parentCategory != null && childCategory != null){
                    // 임시 연결
                    childCategory.setParentCategory(parentCategory);
                    parentCategoryMap.put(parentCategory.getCategoryCode(), parentCategory);
                    childCategoryMap.put(childCategory.getCategoryCode(), childCategory);
                }
            }
        }
        try{
            // 부모와 자식을 나눠서 save하는 방식으로 영속성 지키기!
            categoryRepository.saveAll(parentCategoryMap.values());

            List<Category> childCategoryList = childCategoryMap.values().stream()
                            .map(category -> {
                                Category parentCategory = categoryRepository
                                        .findByCategoryCode(category.getParentCategory().getCategoryCode());
                                category.setParentCategory(parentCategory);
                                return category;
                            }).toList();

            categoryRepository.saveAll(childCategoryList);
        }catch(DataIntegrityViolationException e){
            throw new BusinessException(SpotErrorCode.DUPLICATE_DATA_ERROR);
        }
    }

    /* 초기 저장으로 사용할 카테고리를 위한 클래스! */
    private static class InitCategory{
        String categoryCode;
        int depth;

        InitCategory(String categoryCode, int depth){
            this.categoryCode = categoryCode;
            this.depth = depth;
        }
    }
}
