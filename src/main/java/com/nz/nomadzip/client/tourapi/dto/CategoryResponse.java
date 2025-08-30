package com.nz.nomadzip.client.tourapi.dto;

import java.util.List;

public record CategoryResponse(Response response) {

    public record Response(Header header, Body body) {}

    // Header
    public record Header(String resultCode, String resultMsg) {}

    // Body
    public record Body(Items items, int numOfRows, int pageNo, int totalCount) {}

    // Items
    public record Items(List<Item> item) {}

    // 실제 데이터
    public record Item(String lclsSystm1Cd, String lclsSystm1Nm,
                       String lclsSystm2Cd, String lclsSystm2Nm,
                       String lclsSystm3Cd, String lclsSystm3Nm,
                       int rnum) {}

}
