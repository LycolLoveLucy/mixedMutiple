package com.jielu.filter;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class ApiFilter extends OncePerRequestFilter implements Ordered {

    public static final String REQUEST_ID_MDC_KEY = "requestTraceId";

    public static final String DISTRIBUTION_REQUEST_ID_MDC_KEY = "distributionRequestTraceId";


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        MDC.put(REQUEST_ID_MDC_KEY, UUID.randomUUID().toString());
        //redis.set(DISTRIBUTION_REQUEST_ID_MDC_KEY, UUID.randomUUID().toString())
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            MDC.remove(REQUEST_ID_MDC_KEY);
        }
    }
}