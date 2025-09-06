// FILE: src/main/java/com/rookies4/myspringboot3project/common/ErrorCode.java
package com.rookies4.every_moment.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // AUTH
    INVALID_CREDENTIALS("AUTH_001", "아이디 또는 비밀번호가 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("AUTH_002", "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("AUTH_003", "토큰이 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("AUTH_004", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),

    // VALIDATION & USER
    VALIDATION_ERROR("VALID_001", "입력값이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL("USER_002", "이미 존재하는 이메일입니다", HttpStatus.CONFLICT),
    USER_NOT_FOUND("USER_003", "사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    // SERVER
    INTERNAL_SERVER_ERROR("SERVER_001", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    public final String code;
    public final String message;
    public final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
