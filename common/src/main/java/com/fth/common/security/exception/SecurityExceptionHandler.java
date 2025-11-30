package com.fth.common.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionHandler {

    // 处理Token无效异常
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException e) {
        return new ResponseEntity<>("认证失败: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 处理权限不足异常
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>("权限不足: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }

    // 处理其他安全相关异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleSecurityException(Exception e) {
        return new ResponseEntity<>("安全异常: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}