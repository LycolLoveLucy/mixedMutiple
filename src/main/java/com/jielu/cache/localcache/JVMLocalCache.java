package com.jielu.cache.localcache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JVMLocalCache {

    private  final   ConcurrentLinkedHashMap<String,CachedObject> concurrentLinkedHashMap;

    private  static  final int WATERSHED_SIZE=1000;

    /**
     * second periods it is used for the cached object should be evicted time point
     */
    private  int evictTime=0;

    public  static   final   JVMLocalCache getInstance(int evictTime){
        JVMLocalCache instance=new JVMLocalCache(evictTime);
        return  instance;
    }

    private JVMLocalCache(int evictTime) {
        concurrentLinkedHashMap=ConcurrentLinkedMapHolder.concurrentLinkedHashMap;
        this.evictTime = evictTime;
    }

    public  void put(String key, Object v){
        //you need to do clear work when "put" operation
        clear();
        final long bornTime=System.currentTimeMillis();
        CachedObject cachedObject=new CachedObject(bornTime,v);
        concurrentLinkedHashMap.put(key,cachedObject);

    }

    public  Object get(String key){
        final long now=System.currentTimeMillis();
        CachedObject fetchMe= (CachedObject) concurrentLinkedHashMap.get(key);
        if(fetchMe==null){
            return  null;
        }
        if(((now-fetchMe.getBornTime())/WATERSHED_SIZE)>evictTime){
            concurrentLinkedHashMap.remove(key);
            return null;
        }
        return  fetchMe.getCachedObject();
    }

    final void clear() {
        //watershed size of clear header of  is 10000
        if(concurrentLinkedHashMap.size()<=WATERSHED_SIZE){
            return;
        }
        ExecutorService executorService=  Executors.newFixedThreadPool(1);
        executorService.submit(() -> concurrentLinkedHashMap.removeHeadLoopWithSize(100, 10000));
        executorService.shutdownNow();
    }

    private  static  class  ConcurrentLinkedMapHolder{
        static    ConcurrentLinkedHashMap<String, CachedObject> concurrentLinkedHashMap= new ConcurrentLinkedHashMap<String, CachedObject>();

    }

}
