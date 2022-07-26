package com.jielu.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * <p>
 *
 * This postProcessor it is used for change the log-level for  IOC
 *
 * </p>
 *
 * <code>
 *      @RestController
 *      @NoLog
 *     public NoLogController {
 *
 *     }
 * </code>
 * @Author Lycol
 */
@Component
public class NoLogBeanPostProcessor implements BeanPostProcessor{
    private List<Class> noLogClasses= Collections.synchronizedList(new ArrayList<>());
    final LoggerContext loggerContext =(LoggerContext)LoggerFactory.getILoggerFactory();
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(NoLog.class)){
            noLogClasses.add(bean.getClass());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(!CollectionUtils.isEmpty(noLogClasses)) {
            for (Class clazz : noLogClasses) {
                Logger target = loggerContext.getLogger(clazz);
                target.setLevel(Level.OFF);
            }
        }
        return bean;
    }


}
