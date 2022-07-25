package com.jielu.util;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class OrderUtil {

    private OrderUtil(){}

    private static final long workerIdBits = 5L;
    private static final long datacenterIdBits = 5L;
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private static final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private static  final long  workerId=getMaxWorkerId(maxWorkerId,maxDatacenterId);

    public static final FastDateFormat FULL_DATE_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmssS");

    public static  final ThreadLocalRandom  threadLocalRandom=ThreadLocalRandom.current();

    public static String genDateYYMMssYYYRandomOrderCode(String prefix){
       String dateFormatterStr= FULL_DATE_FORMAT.format(new Date());
       long  randomNextInt= threadLocalRandom.nextInt(1000,9999);
       return prefix+dateFormatterStr+workerId+randomNextInt;

    }

    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.isNotBlank(name)) {
            /*
             * GET jvmPid
             */
            mpid.append(name.split(StringPool.AT)[0]);
        }
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    public static void main(String[] args) {
        int sampleCodeLen=genDateYYMMssYYYRandomOrderCode("task").length();
       for(int i=1;i<=100000;i++){
           int eachGenCodeLen=genDateYYMMssYYYRandomOrderCode("task").length();
           if(sampleCodeLen!=eachGenCodeLen){
               throw  new RuntimeException("un passed");
           }
       }
    }

}
