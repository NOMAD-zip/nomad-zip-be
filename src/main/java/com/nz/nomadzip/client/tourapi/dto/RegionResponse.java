package com.nz.nomadzip.client.tourapi.dto;

public record RegionResponse(
        Response response
) {
    public record Response(
            Header header,
            Body body
    ) {}

    public record Header(
            String resultCode,
            String resultMsg
    ) {}

    public record Body(
            Items items,
            int numOfRows,
            int pageNo,
            int totalCount
    ) {}

    public record Items(
            java.util.List<Item> item
    ) {}

    public record Item(
            int rnum,
            String code,
            String name
    ) {}
}
