package com.jielu.feign;

import com.jielu.web.request.FeignGetRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//@FeignClient(name = "api",url = "http://localhost:8080/feign",configuration = SpringDecoderConfiguration.class)
public interface FeignEnhanceClient {

    @RequestMapping(value = "/gettingWithRequestBody", method = RequestMethod.GET)
    @ResponseBody
    String gettingWithRequestBody(FeignGetRequest feignGetRequest);

}