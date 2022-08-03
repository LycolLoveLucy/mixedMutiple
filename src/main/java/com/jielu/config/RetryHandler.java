package com.jielu.config;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Retry Handler
 */
public class RetryHandler implements  Retry{

    private static final Logger logger = LoggerFactory.getLogger("retrying");
    TimeUnit time = TimeUnit.MICROSECONDS;

    @Override
    public Object retryMethod( MethodInvocation invocation) throws Throwable {
       Method method=invocation.getMethod();
        RetryFlag retry=method.getAnnotation(RetryFlag.class);
        int retryCnt=retry.number();
        Object res=null;
         while (retryCnt>=1){
             time.sleep(50);
             try {
               res=  invocation.proceed();
             }
             catch (Exception ex){
                 if(!canRetry(retry,ex)){
                    break;
                 }
                 --retryCnt;
                 logger.warn("the method name"+method.getName()+"has been retry "+(retry.number()-retryCnt)+" times");


             }
             return  res;
         }
         return  invocation.proceed();
    }

    private  boolean canRetry(RetryFlag retry,Exception exception){
        for(Class c:retry.triggerExceptionClass()){
            if(exception.getClass().equals(c)){
                return true;

            }
        }
      return  false;

    }

}
