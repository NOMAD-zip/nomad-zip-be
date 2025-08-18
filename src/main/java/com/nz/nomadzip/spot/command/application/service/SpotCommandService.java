package com.nz.nomadzip.spot.command.application.service;

import com.nz.nomadzip.client.tourapi.dto.RegionResponse;
import com.nz.nomadzip.client.tourapi.port.TourApiPort;
import com.nz.nomadzip.spot.command.application.mapper.RegionMapper;
import com.nz.nomadzip.spot.command.domain.entity.Region;
import com.nz.nomadzip.spot.command.domain.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SpotCommandService {
    private final RegionRepository regionRepository;
    private final TourApiPort tourApi;
    private final RegionMapper regionMapper;

    @Transactional
    public void fetchRegion() {
        List<Region> regionList = new ArrayList<>();    // 최종 저장 시 사용할 지역 배열

        // 부모 지역 호출
        RegionResponse parentRegions = tourApi.getAreaCodes(100);

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
}
