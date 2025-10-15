package com.nz.nomadzip.group.exception;

import com.nz.nomadzip.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum GroupErrorCode implements ErrorCode {
    /* ????? 에러코드 할당 */
    ALREADY_JOIN("71001", "이미 그룹의 멤버입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
