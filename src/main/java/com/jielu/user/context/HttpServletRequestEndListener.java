package com.jielu.user.context;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * Do remove current User Context
 */
@Component
public class HttpServletRequestEndListener implements ApplicationListener<ServletRequestHandledEvent> {

    @Override
    public void onApplicationEvent(ServletRequestHandledEvent event) {
        UserConTextThreadLocalHolder.remove();
    }

}
