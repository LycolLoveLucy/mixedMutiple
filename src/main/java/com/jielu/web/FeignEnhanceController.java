package com.jielu.web;

import com.jielu.web.request.FeignGetRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/feign")
public class FeignEnhanceController {

   /* @Resource
    FeignEnhanceClient feignEnhanceClient;*/

    @GetMapping("gettingWithRequestBody")
    public  String gettingRequestWithBody(FeignGetRequest feignGetRequest){

        return  feignGetRequest.toString();
    }


   /* @GetMapping("testGettingRequestWithFeignBody")
    public  String testGettingRequestWithFeignBody(FeignGetRequest feignGetRequest){

        return  feignEnhanceClient.gettingWithRequestBody(feignGetRequest);
    }*/
}
