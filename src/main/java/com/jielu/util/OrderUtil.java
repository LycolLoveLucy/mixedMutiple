package com.jielu.util;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class OrderUtil {

    private OrderUtil(){}

    private static final long workerIdBits = 5L;
    private static final long datacenterIdBits = 5L;
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private static final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private static  final long  workerId=getMaxWorkerId(maxWorkerId,maxDatacenterId);

    public static final FastDateFormat FULL_DATE_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");

    public static  final ThreadLocalRandom  threadLocalRandom=ThreadLocalRandom.current();

    private static final  int  FIXED_LEN=4;

    public static String genDateYYMMssRandomOrderCode(String prefix){
       String dateFormatterStr= FULL_DATE_FORMAT.format(new Date());
       int  randomNextInt= threadLocalRandom.nextInt(1000,9999);
       String randomNextIntStr=randomNextInt+"";
       for (int i=1;i<=FIXED_LEN-randomNextIntStr.length();i++){
           randomNextIntStr="0"+randomNextIntStr;

       }
       return prefix+workerId+dateFormatterStr+randomNextIntStr;

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



}
