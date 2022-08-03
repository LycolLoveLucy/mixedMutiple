package com.jielu.config;

import com.jielu.cache.localcache.JVMLocalCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalCachedMapConfig {

    @Bean
    JVMLocalCache jvmLocalCache(){
        return  JVMLocalCache.getInstance(60*60*24);
    }



}
