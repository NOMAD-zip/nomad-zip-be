package com.nz.nomadzip.like.command.application.controller;

import com.nz.nomadzip.common.dto.ApiResponse;
import com.nz.nomadzip.like.command.application.dto.request.LikeRequest;
import com.nz.nomadzip.like.command.application.service.LikeCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lodgings")
@RequiredArgsConstructor
public class LikeCommandController {

    private final LikeCommandService likeCommandService;

    @PostMapping("/{lodgingId}/like")
    public ResponseEntity<ApiResponse<Void>> like(@PathVariable Long lodgingId) {
        Long userId = 1L;
        likeCommandService.registerLike(LikeRequest.builder()
                .lodgingId(lodgingId).userId(userId).build());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{lodgingId}/like")
    public ResponseEntity<ApiResponse<Void>> unlike(@PathVariable Long lodgingId) {
        Long userId = 1L;
        likeCommandService.cancelLike(LikeRequest.builder()
                .lodgingId(lodgingId).userId(userId).build());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}