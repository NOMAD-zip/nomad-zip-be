package com.nz.nomadzip.client.tourapi.port;

import com.nz.nomadzip.client.tourapi.dto.RegionResponse;

public interface TourApiPort {
    RegionResponse getAreaCodes(int numOfRows);

    RegionResponse getAreaCode(String parentAreaCode, int numOfRows);
}
