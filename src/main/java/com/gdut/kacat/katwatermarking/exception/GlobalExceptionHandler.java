package com.gdut.kacat.katwatermarking.exception;

import com.gdut.kacat.katwatermarking.entity.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result handleException(Exception e) {
        return Result.error(e.getMessage());
    }
}
