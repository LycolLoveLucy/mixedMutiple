package com.jielu.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Time sliding window for limitation-Requesting situation
 *
 * @Author:Lycol
 */
public class LimitationRequestStrategy {

    private static final String REQUEST_REJECTED_MESSAGE =
            "Your current request attention has been rejected by the server," +
                    "It is not be allowed  request  %d times within  %d minutes";

    /**
     * Limitation size
     */
    private int limitationSize;


    /**
     * Time unit Second
     */
    private int timeSlidingDownSize;

    Map<String, AtomicReference<Pair>> limitationConcurrentMap = new ConcurrentHashMap<>();


    public LimitationRequestStrategy(int limitationSize, int timeSlidingDownSize) {
        this.limitationSize = limitationSize;
        this.timeSlidingDownSize = timeSlidingDownSize;
    }


    public void request(String token) {

        long systemNow = System.currentTimeMillis();
        if (limitationConcurrentMap.get(token) == null) {
            AtomicReference atomicReference = new AtomicReference();
            atomicReference.set(new Pair(systemNow));
            limitationConcurrentMap.put(token, atomicReference);
            return;
        }

        Pair pair = limitationConcurrentMap.get(token).get();
        if (getSecondsOf(pair.getLastRequestTime(), systemNow) > limitationSize * 60) {
            pair.reset();
        }
        else {
            if (pair.getRequestedTime() >= limitationSize) {
                pair.reset();
                throw new RuntimeException(String.format(REQUEST_REJECTED_MESSAGE, limitationSize, timeSlidingDownSize));
            }
            pair.inc();
        }

    }

    private static long getSecondsOf(Long startTime, Long systemCurrentTime) {
        return ((systemCurrentTime -startTime ) / 1000);
    }


    public static final class Pair {
        public Pair(Long lastRequestTime) {
            this.lastRequestTime = lastRequestTime;
            atomicInteger.set(1);
        }

        AtomicInteger atomicInteger = new AtomicInteger(0);

        private Long lastRequestTime;

        public AtomicInteger getAtomicInteger() {
            return atomicInteger;
        }


        public int getRequestedTime() {
            return atomicInteger.get();
        }

        public void reset() {
            lastRequestTime = System.currentTimeMillis();
            atomicInteger.set(0);
        }

        public void inc() {
            atomicInteger.incrementAndGet();
        }

        public Long getLastRequestTime() {
            return lastRequestTime;
        }

    }


}

