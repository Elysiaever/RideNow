package com.fth.common.exception;

import com.fth.common.api.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<?>> handleBusinessException(BusinessException ex) {
        Response<?> body = Response.error(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getCode()).body(body);  // 设置 HTTP 状态码
    }

    // 处理所有未知异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleException(Exception ex) {
        ex.printStackTrace();
        Response<?> body = Response.error(500, "服务器内部错误：" + ex.getMessage());
        return ResponseEntity.status(500).body(body);  // 设置 HTTP 状态码
    }
}

