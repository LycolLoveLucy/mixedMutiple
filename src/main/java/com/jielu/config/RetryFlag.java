package com.jielu.config;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetryFlag {

    Class[] triggerExceptionClass() default {RuntimeException.class};


    int number() default  1;


}
