package com.nz.nomadzip.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
    private int currentPage;
    private int totalPage;
    private int totalCount;
    private int size;
}