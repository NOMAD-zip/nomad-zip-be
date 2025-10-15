package com.nz.nomadzip.spot.command.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/* TODO 검증 로직 추가하기 */
@Getter
@NoArgsConstructor
public class CreateGroupRequest {

    /* 생성 요청 유저 정보 */
    private Long userId;

    /* 선택한 관광지 목록 */
    private List<Integer> spotIdList;

    /* 시작일 */
    private Date startedAt;

    /* 종료일 */
    private Date endedAt;

    /* 최대 참여 가능 인원 */
    private int maxParticipants;

    /* 혹시 결제 추가 되면 참가비 추가하기! */

}
