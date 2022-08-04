package com.jielu.log;

import com.jielu.leetcode.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncLogger implements Log {


    private final Logger log;

    public AsyncLogger(String clazz) {
        log =    LoggerFactory.getLogger(clazz);
    }


    public AsyncLogger(Class clazz) {
        log =    LoggerFactory.getLogger(clazz);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }


    @Override
    public void error(String s, Throwable e) {
       Future<?> future= Executors.newSingleThreadExecutor(
               new NamedThreadFactory("sync-logging"))
               .submit(() -> log.error(s,e)
               );
       if(future.isCancelled()){
           log.error( "Log thread is busing");
       }
    }

    @Override
    public void error(String s) {
        Future<?> future= Executors.newSingleThreadExecutor(
                        new NamedThreadFactory("sync-logging-error"))
                .submit(() -> log.error(s)
                );
        if(future.isCancelled()){
            log.error("Log thread is busing");
        }
    }

    @Override
    public void error(String format, Object... params) {
        Future<?> future= Executors.newSingleThreadExecutor(
                        new NamedThreadFactory("sync-logging"))
                .submit(() -> log.error(replaceOverFlowSpace(format),params)
                );
        if(future.isCancelled()){
            log.debug("Log thread is busing");
        }
    }

    @Override
    public void debug(String s) {
        Future<?> future= Executors.newSingleThreadExecutor(
                        new NamedThreadFactory("sync-logging"))
                .submit(() -> log.debug(s)
                );
        if(future.isCancelled()){
            log.debug("Log thread is busing");
        }
    }

    @Override
    public void trace(String s) {
        Future<?> future= Executors.newSingleThreadExecutor(
                        new NamedThreadFactory("sync-logging-trace"))
                .submit(() -> log.trace(s)
                );
        if(future.isCancelled()){
            log.trace("Log trace is busing");
        }
    }

    @Override
    public void trace(String format, Object... params) {
        Future<?> future= Executors.newSingleThreadExecutor(
                        new NamedThreadFactory("sync-logging-trace"))
                .submit(() -> log.trace(replaceOverFlowSpace(format),params)
                );
        if(future.isCancelled()){
            log.trace("Log trace is busing");
        }
    }

    @Override
    public void warn(String s) {
        Future<?> future= Executors.newSingleThreadExecutor(
                        new NamedThreadFactory("sync-logging-waring"))
                .submit(() -> log.warn(s)
                );
        if(future.isCancelled()){
            log.warn("Log thread is busing");
        }
    }

    @Override
    public void warn(String format, Object... params) {
        Future<?> future= Executors.newSingleThreadExecutor(
                        new NamedThreadFactory("sync-logging-waring"))
                .submit(() -> log.warn(replaceOverFlowSpace(format),params));

        if(future.isCancelled()){
            log.warn("Log thread is busing");
    }


}

    @Override
    public void info(String s) {
        Future<?> future= Executors.newSingleThreadExecutor(
                        new NamedThreadFactory("sync-logging-waring"))
                .submit(() -> log.info(s));

        if(future.isCancelled()){
            log.warn("Logging thread is busing");
        }
    }

    @Override
    public void info(String format, Object... params) {
        Future<?> future= Executors.newSingleThreadExecutor(
                        new NamedThreadFactory("sync-logging-waring"))
                .submit(() -> log.info(replaceOverFlowSpace(format),params));

        if(future.isCancelled()){
            log.info("Log thread is busing");

        }
    }


    /**
     * such as "I am { } JAVA Or C++","developer"  it will be printing "I am JAVA Or C++ developer"
     * @param format
     * @return
     */
    static String  replaceOverFlowSpace(String format){
      return   format.replaceAll("[{]+[\\s+]+[}]","{}");
    }

    public static void main(String[] args) {
        AsyncLogger asyncLogger=new AsyncLogger("test");
        System.out.println("{ }123".replaceAll("[{]+[\\s+]+[}]","123"));

        asyncLogger.info("123 {432},{  }",6,7);
    }

}
