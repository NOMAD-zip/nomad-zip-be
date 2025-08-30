package com.nz.nomadzip.spot.command.application.controller;

import com.nz.nomadzip.common.dto.ApiResponse;
import com.nz.nomadzip.spot.command.application.service.SpotCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spot") // 채우기
public class SpotCommandController {
    private final SpotCommandService spotCommandService;

    /* 지역 정보 초기 채우기용 */
    @GetMapping("/region")
    public ResponseEntity<ApiResponse<Void>> fetchRegion(){
        spotCommandService.fetchRegion();

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<Void>> fetchCategory(){
        spotCommandService.fetchCategory();

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
