package com.chen.aigis.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.chen.spring.base.data.ControllerUtil.fail;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> runtimeExceptionHandler(Exception e) {
        e.printStackTrace();
        return fail();
    }
}
