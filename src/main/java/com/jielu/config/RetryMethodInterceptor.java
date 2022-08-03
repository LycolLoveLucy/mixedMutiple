package com.jielu.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


public class RetryMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        RetryHandler retryHandler=new RetryHandler();
        try {
         invocation.proceed();
        }
        catch (Exception e){
            return  retryHandler.retryMethod(invocation);

        }
       return invocation.proceed();
    }
}
