package com.jielu.util;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class RedisCreateOrderUtil {

    @Autowired(required = false)
    RedisTemplate redisTemplate;

    static final String REDIS_INC_KEY="org:lylcol:order:redis:inc";

    /**
     * 用Redis计数器生成订单号,生成规则:前缀+年月日+Redis自增长序列号
     * 劣势：依赖于Redis,消耗带宽,消耗流量.
     * 优势：保证有序,而且每天的订单号增长都是从1开始自增的
     * @param prefix 前缀字符
     * @return 订单号 eg:Order201906110002
     */
    public String genOrderNoByRedis(String prefix){
        RedisAtomicLong redisAtomicLong= new RedisAtomicLong(REDIS_INC_KEY, redisTemplate.getConnectionFactory());
        Long increment = redisAtomicLong.incrementAndGet();
        Calendar cs= Calendar.getInstance();
        //凌晨过期
        cs.add(Calendar.DAY_OF_MONTH,1);
        cs.set(Calendar.HOUR_OF_DAY,0);
        cs.set(Calendar.MINUTE,0);
        cs.set(Calendar.SECOND,0);
        //设置第二天的凌晨00：00为失效期
        if(increment==1L) {
            redisAtomicLong.expireAt(cs.getTime());
        }
        //补四位,缺失的位置用0补位
        String  importantKey=increment.toString();
        if(importantKey.length()<4) {
            importantKey = "0000".substring(0,4-importantKey.length())+importantKey;
        }
        //
        String orderNO=String.format("%s%s%s",prefix, FastDateFormat.getInstance("yyyyMMdd").format(cs.getTime()),importantKey);
        return orderNO;

    }
}
