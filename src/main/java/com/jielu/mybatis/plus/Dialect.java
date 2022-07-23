package com.jielu.mybatis.plus;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Dialect {

    /**
     * default is mysql
     * @return
     */
    Type type() default  Type.MYSQL;
}
