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

    @GetMapping("/region")
    public ResponseEntity<ApiResponse<Void>> fetchRegion(){
        spotCommandService.fetchRegion();

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
