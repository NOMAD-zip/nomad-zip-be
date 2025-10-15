package com.nz.nomadzip.group.command.application.controller;

import com.nz.nomadzip.common.dto.ApiResponse;
import com.nz.nomadzip.group.command.application.service.GroupCommandService;
import com.nz.nomadzip.spot.command.application.dto.request.CreateGroupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("group") // 채우기
public class GroupCommandController {
    private final GroupCommandService groupCommandService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createGroup(
            @RequestBody CreateGroupRequest createGroupRequest
    ){
        groupCommandService.createGroup(createGroupRequest);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
