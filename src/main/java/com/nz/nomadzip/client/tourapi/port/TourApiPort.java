package com.nz.nomadzip.client.tourapi.port;

import com.nz.nomadzip.client.tourapi.dto.CategoryResponse;
import com.nz.nomadzip.client.tourapi.dto.RegionResponse;

public interface TourApiPort {
    RegionResponse getAreaCode(int numOfRows);

    RegionResponse getAreaCode(String parentAreaCode, int numOfRows);

    CategoryResponse getCategory(String categoryCode, int depth);

}
