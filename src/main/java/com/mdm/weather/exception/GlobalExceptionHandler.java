package com.mdm.weather.exception;

import com.mdm.weather.common.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseResult<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult<String> handleException(Exception e) {
        return ResponseResult.error("服务器内部错误：" + e.getMessage());
    }
} 