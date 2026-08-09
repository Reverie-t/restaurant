package org.example.restaurantmanagement.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.restaurantmanagement.dto.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {

        log.warn("业务异常：{}", e.getMessage());

        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {

        log.error("系统异常", e);

        return Result.error("服务器内部异常，请稍后重试");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e){

        String message = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return Result.error(message);

    }
}