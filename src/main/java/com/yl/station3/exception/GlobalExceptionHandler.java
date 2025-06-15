package com.yl.station3.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("BusinessException", e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }

    /**
     * javax.validation.Valid 또는 @Validated 바인딩 오류 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        final List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.FieldError.of(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());
        
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, fieldErrors);
        return new ResponseEntity<>(response, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    /**
     * @ModelAttribute 바인딩 오류 처리
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("BindException", e);
        final List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.FieldError.of(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());
        
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, fieldErrors);
        return new ResponseEntity<>(response, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    /**
     * enum type 일치하지 않아 binding 못할 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE);
        return new ResponseEntity<>(response, ErrorCode.INVALID_TYPE_VALUE.getStatus());
    }

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
        return new ResponseEntity<>(response, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    /**
     * 필수 요청 파라미터 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER, e.getMessage());
        return new ResponseEntity<>(response, ErrorCode.MISSING_REQUEST_PARAMETER.getStatus());
    }

    /**
     * 인증되지 않은 요청 (401 Unauthorized)
     */
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException e, HttpServletRequest request) {
        log.error("AuthenticationException: {}", e.getMessage());
        
        final ErrorResponse response = ErrorResponse.builder()
                .message("인증이 필요합니다. 로그인 후 다시 시도해주세요.")
                .code("AUTH_001")
                .status(ErrorCode.AUTHENTICATION_FAILED.getStatus().value())
                .error(ErrorCode.AUTHENTICATION_FAILED.getStatus().getReasonPhrase())
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(response, ErrorCode.AUTHENTICATION_FAILED.getStatus());
    }

    /**
     * 잘못된 인증 정보 (로그인 실패)
     */
    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException e, HttpServletRequest request) {
        log.error("BadCredentialsException: {}", e.getMessage());
        
        final ErrorResponse response = ErrorResponse.builder()
                .message("이메일 또는 비밀번호가 올바르지 않습니다.")
                .code("AUTH_002")
                .status(ErrorCode.AUTHENTICATION_FAILED.getStatus().value())
                .error(ErrorCode.AUTHENTICATION_FAILED.getStatus().getReasonPhrase())
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(response, ErrorCode.AUTHENTICATION_FAILED.getStatus());
    }

    /**
     * 접근 권한 부족 (403 Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        log.error("AccessDeniedException: {}", e.getMessage());
        
        final ErrorResponse response = ErrorResponse.builder()
                .message("해당 리소스에 접근할 권한이 없습니다.")
                .code("AUTH_003")
                .status(ErrorCode.ACCESS_DENIED.getStatus().value())
                .error(ErrorCode.ACCESS_DENIED.getStatus().getReasonPhrase())
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(response, ErrorCode.ACCESS_DENIED.getStatus());
    }

    /**
     * Bean Validation 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getMessage());
        return new ResponseEntity<>(response, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    /**
     * 데이터 무결성 위반
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.DATABASE_ERROR);
        return new ResponseEntity<>(response, ErrorCode.DATABASE_ERROR.getStatus());
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getMessage());
        return new ResponseEntity<>(response, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    /**
     * 예상하지 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
    }
}
