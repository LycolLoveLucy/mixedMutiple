package com.jielu.config;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MethodRetryInterceptorConfig {
    public static final String traceExecution = "execution (* com.jielu.service..*.*(..)) && " +
            "@annotation(com.jielu.config.RetryFlag)";

    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor() {
        RetryMethodInterceptor methodInterceptor = new RetryMethodInterceptor();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(traceExecution);



        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(methodInterceptor);
        return advisor;
    }
}

