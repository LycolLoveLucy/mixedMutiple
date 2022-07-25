package com.jielu.xxljob.ruote.strategy.enhance;


import com.jielu.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * There are many distributed machines recommend you use this LRU strategy and replace the old
 *
 */
public class ExecutorRouteLRU extends ExecutorRouter {
    private static ConcurrentMap<Integer, LinkedHashMap<String, String>> jobLRUMap = new ConcurrentHashMap<>();
    private static long CACHE_VALID_TIME = 0;

    private static  final int TIME_OF_ONE_DAY= 1000*60*60*24;
    public String route(int jobId, List<String> addressList) {
        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            jobLRUMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + TIME_OF_ONE_DAY;
        }
        // init lru
        LinkedHashMap<String, String> lruItem = jobLRUMap.get(jobId);
        if (lruItem == null) {
            /**
             * accessOrder is true means the keySet order by access
             */
            lruItem = new LinkedHashMap<>(16, 0.75f, true);
            jobLRUMap.putIfAbsent(jobId, lruItem);
        }

        synchronized (addressList.getClass()) {
            // put new
            if (CollectionUtils.isNotEmpty(addressList)) {
                for (String address : addressList) {
                    lruItem.putIfAbsent(address, address);
                }
            }
        }

        // load
        String eldestKey = lruItem.entrySet().iterator().next().getKey();
        String eldestValue = lruItem.get(eldestKey);
        return eldestValue;
    }

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        String address = route(triggerParam.getJobId(), addressList);
        return new ReturnT<>(address);
    }

}
