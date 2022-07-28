package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.admin.core.route.ExecutorRouter;
import com.xxl.job.admin.core.route.localcache.JVMLocalCache;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 分组下机器地址相同，不同JOB均匀散列在不同机器上，保证分组下机器分配JOB平均；且每个JOB固定调度其中一台机器；
 *      a、virtual node：解决不均衡问题
 *      b、hash method replace hashCode：String的hashCode可能重复，需要进一步扩大hashCode的取值范围
 * Created by xuxueli on 17/3/10.
 * Enhnace by Lycol addTemp
 */
public class ExecutorRouteConsistentHash extends ExecutorRouter {

    private static int VIRTUAL_NODE_NUM = 100;

    private static ThreadLocal<MessageDigest> MD = new ThreadLocal<>();

    private  static  final JVMLocalCache jvmLocalCache=JVMLocalCache.getInstance(60*60*1);

    private static final String templateAddressHashFormatStr="SHARD-%s-NODE-%d";


    /**
     * get hash code on 2^32 ring (md5散列的方式计算hash值)
     * @param key
     * @return
     */
    private static long hash(String key) {

        // md5 byte
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            MD.set(md5);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }

        byte[] digest;
        try {
            digest = md5.digest(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5 not supported", e);
        }

        long truncateHashCode = getHashCode(digest);
        return truncateHashCode;
    }

    private static long getHashCode(byte[] digest) {
        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF)& 0xffffffffL;
        return hashCode;
    }

    public String hashJob(int jobId, List<String> addressList) {

        // ------A1------A2-------A3------
        // -----------J1------------------
        TreeMap<Long, String> addressRing = new TreeMap<>();
        for (String address: addressList) {

            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                String typedKey=String.format(templateAddressHashFormatStr,address,i);
                long addressHash=0L;
                Object cachedAddressHash=jvmLocalCache.get(typedKey);
                if(null!=cachedAddressHash){
                    addressHash= (long) cachedAddressHash;
                }
                else {
                    addressHash = hash(typedKey);
                }
                jvmLocalCache.put(typedKey,addressHash);
                addressRing.put(addressHash, address);
            }

        }

        long jobHash = hash(String.valueOf(jobId));
        SortedMap<Long, String> lastRing = addressRing.tailMap(jobHash);
        if (!lastRing.isEmpty()) {
            return lastRing.get(lastRing.firstKey());
        }
        return addressRing.ceilingEntry(jobHash).getValue();
    }

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        String address = hashJob(triggerParam.getJobId(), addressList);
        return new ReturnT<String>(address);
    }



}
