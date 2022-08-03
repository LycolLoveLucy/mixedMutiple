package com.jielu.config;

import org.aopalliance.intercept.MethodInvocation;

public interface Retry {

    Object retryMethod(MethodInvocation invocation) throws Throwable;


}

