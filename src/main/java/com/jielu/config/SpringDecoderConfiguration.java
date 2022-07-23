package com.jielu.config;

import com.jielu.feign.FeignRequestSupportRequestBodySpringEncoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDecoderConfiguration {
    @Bean
    public Encoder feignEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new FeignRequestSupportRequestBodySpringEncoder(messageConverters);
    }
}
