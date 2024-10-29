package com.simba.quartz.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdviceHandler {
    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
    }
}
