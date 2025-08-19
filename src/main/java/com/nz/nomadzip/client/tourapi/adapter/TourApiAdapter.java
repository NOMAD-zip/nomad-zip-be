package com.nz.nomadzip.client.tourapi.adapter;

import com.nz.nomadzip.client.tourapi.dto.RegionResponse;
import com.nz.nomadzip.client.tourapi.port.TourApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
@RequiredArgsConstructor
public class TourApiAdapter implements TourApiPort {
    private final WebClient webClient;

    @Override
    public RegionResponse getAreaCodes(int numOfRows) {
        RegionResponse regionResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/areaCode2")
                        .queryParam("numOfRows",numOfRows)
                        .build()
                )
                .retrieve()
                .bodyToMono(RegionResponse.class)
                .block();
        return regionResponse;
    }

    @Override
    public RegionResponse getAreaCode(String parentAreaCode, int numOfRows) {
        RegionResponse childRegionResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/areaCode2")
                        .queryParam("numOfRows",numOfRows)
                        .queryParam("areaCode", parentAreaCode)
                        .build()
                )
                .retrieve()
                .bodyToMono(RegionResponse.class)
                .block();
        return childRegionResponse;
    }
}
