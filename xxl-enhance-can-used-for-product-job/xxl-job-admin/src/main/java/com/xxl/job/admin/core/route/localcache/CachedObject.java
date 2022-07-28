package com.xxl.job.admin.core.route.localcache;

import java.io.Serializable;

public class CachedObject implements Serializable {

    /**
     * born time
     */
    private  long bornTime;

    /**
     * The cached Object
     */
    private  Object cachedObject;

    public CachedObject(long bornTime, Object cachedObject) {
        this.bornTime = bornTime;
        this.cachedObject = cachedObject;
    }

    public long getBornTime() {
        return bornTime;
    }

    public Object getCachedObject() {
        return cachedObject;
    }


}
