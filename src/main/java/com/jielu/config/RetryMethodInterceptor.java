package com.jielu.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class RetryMethodInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger("retrying-Method");
    TimeUnit time = TimeUnit.MICROSECONDS;
    AtomicInteger atomicInteger = new AtomicInteger();
    AtomicBoolean whileCondition = new AtomicBoolean(true);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        RetryFlag retry = method.getAnnotation(RetryFlag.class);
        int retryCnt = retry.number();
        if (atomicInteger.get() != retryCnt) {
            atomicInteger.set(retryCnt);
        }
        Object res = null;
        Exception ex = null;
        while (whileCondition.get() && atomicInteger.get() >= 1) {
            try {
                res = invocation.proceed();
            } catch (Exception e) {
                ex = e;
                if (canRetry(retry, e)) {
                    logger.warn("the method named ->" + method.getName() + " has been retried " + (retry.number() + 1 - atomicInteger.get()) + " times");
                    time.sleep(10);
                    atomicInteger.decrementAndGet();
                    continue;
                } else {
                    whileCondition.set(false);
                }
            }

        }
        if (ex != null) {
            throw ex;
        }

        return res;

    }

    private boolean canRetry(RetryFlag retry, Exception exception) {
        for (Class c : retry.triggerExceptionClass()) {
            if (exception.getClass().equals(c)) {
                return true;

            }
        }
        return false;

    }
}