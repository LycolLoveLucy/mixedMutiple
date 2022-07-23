package com.jielu.advice;

import com.jielu.web.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionAdvice {

    @ExceptionHandler(RuntimeException.class)
    public R runtimeExceptionHandler(RuntimeException runtimeException){
        return  R.fail(null,runtimeException.getMessage());
    }
}
