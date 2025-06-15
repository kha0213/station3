package com.yl.station3.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    
    private final String message;
    private final String code;
    private final int status;
    private final String error;
    private final String path;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime timestamp;
    
    private final List<FieldError> errors;
    
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .status(errorCode.getStatus().value())
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .message(message)
                .code(errorCode.getCode())
                .status(errorCode.getStatus().value())
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static ErrorResponse of(ErrorCode errorCode, List<FieldError> errors) {
        return ErrorResponse.builder()
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .status(errorCode.getStatus().value())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }
    
    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;
        
        public static FieldError of(String field, String value, String reason) {
            return FieldError.builder()
                    .field(field)
                    .value(value)
                    .reason(reason)
                    .build();
        }
    }
}
