package com.yl.station3.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    
    // 400 Bad Request
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E001", "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "E002", "잘못된 타입입니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "E003", "필수 요청 파라미터가 누락되었습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "E004", "올바른 이메일 형식이 아닙니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "E005", "비밀번호는 6자 이상 20자 이하여야 합니다."),
    INVALID_PRICE_FORMAT(HttpStatus.BAD_REQUEST, "E006", "가격 형식이 올바르지 않습니다."),
    
    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E100", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "E101", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "E102", "만료된 토큰입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "E103", "인증에 실패했습니다."),
    
    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "E200", "권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "E201", "해당 리소스에 접근할 권한이 없습니다."),
    
    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E300", "사용자를 찾을 수 없습니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "E301", "방을 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E302", "요청한 리소스를 찾을 수 없습니다."),
    
    // 409 Conflict
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "E400", "이미 사용 중인 이메일입니다."),
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "E401", "이미 존재하는 리소스입니다."),
    
    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E501", "데이터베이스 오류가 발생했습니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
    
    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
