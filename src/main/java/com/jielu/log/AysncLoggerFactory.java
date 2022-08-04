package com.jielu.log;

public class AysncLoggerFactory {

    public  static  Log getAsyncLogger(Class clazz){
        AsyncLogger aysncLogger=new AsyncLogger(clazz);
        return aysncLogger;
    }


    public  static  Log getAsyncLogger(String clazz){
        AsyncLogger aysncLogger=new AsyncLogger(clazz);
        return aysncLogger;
    }
}
