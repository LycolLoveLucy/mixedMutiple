package com.jielu.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestTemplate;
import feign.Util;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.*;


/**
 * @author Lycol
 * @purpose support request with get method can add requestBody parameter in feign
 */
public class FeignRequestSupportRequestBodySpringEncoder implements Encoder {
    Logger log= LoggerFactory.getLogger(FeignRequestSupportRequestBodySpringEncoder.class);
    private ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignRequestSupportRequestBodySpringEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Override
    public void encode(Object requestBody, Type bodyType, RequestTemplate request)
            throws EncodeException {
        if (requestBody != null) {
            Class<?> requestType = requestBody.getClass();
            Collection<String> contentTypes = request.headers().get("Content-Type");

            MediaType requestContentType = null;
            if (contentTypes != null && !contentTypes.isEmpty()) {
                String type = contentTypes.iterator().next();
                requestContentType = MediaType.valueOf(type);
            }

            for (HttpMessageConverter<?> messageConverter : this.messageConverters
                    .getObject().getConverters()) {
                if (messageConverter.canWrite(requestType, requestContentType)) {
                    if (log.isDebugEnabled()) {
                        if (requestContentType != null) {
                            log.debug("Writing [" + requestBody + "] as \""
                                    + requestContentType + "\" using ["
                                    + messageConverter + "]");
                        }
                        else {
                            log.debug("Writing [" + requestBody + "] using ["
                                    + messageConverter + "]");
                        }

                    }

                    FeignOutputMessage outputMessage = new FeignOutputMessage(request);
                    try {
                        HttpMessageConverter<Object> copy = (HttpMessageConverter<Object>) messageConverter;
                        copy.write(requestBody, requestContentType, outputMessage);
                    }
                    catch (IOException ex) {
                        throw new EncodeException("Error converting request body", ex);
                    }
                    // clear headers
                    request.headers(null);
                    // converters can modify headers, so update the request
                    // with the modified headers
                    request.headers(convertHttpHeaderToMap(outputMessage.getHeaders()));
                    if(HttpMethod.resolve(request.method())==HttpMethod.GET) {
                    if(requestBody !=null) {
                        Map<String, Object> requestMap =getParsedChainMap(objectToMap(requestBody));
                        for (Map.Entry<String,Object> entry : requestMap.entrySet()) {
                            if (null != entry.getValue()) {
                                request.query(entry.getKey(), entry.getValue().toString());
                            }
                        }
                    }
                        request.body();
                    }

                    else {
                        request.body(outputMessage.getOutputStream().toByteArray(), Util.UTF_8);
                    }
                    return;
                }
            }
            String message = "Could not write request: no suitable HttpMessageConverter "
                    + "found for request type [" + requestType.getName() + "]";
            if (requestContentType != null) {
                message += " and content type [" + requestContentType + "]";
            }
            throw new EncodeException(message);
        }
    }

    private class FeignOutputMessage implements HttpOutputMessage {

        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        private final HttpHeaders httpHeaders;

        private FeignOutputMessage(RequestTemplate request) {
            httpHeaders = convertRequestHeaderToHttpHeader(request);
        }

        @Override
        public OutputStream getBody() throws IOException {
            return this.outputStream;
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.httpHeaders;
        }

        public ByteArrayOutputStream getOutputStream() {
            return this.outputStream;
        }

    }

    private static Map<String,Object> objectToMap(Object requestBody){
        ObjectMapper objectMapper=new ObjectMapper();
        Map<String, Object> objectAsMap = objectMapper.convertValue(requestBody, Map.class);
        return  objectAsMap;
    }

    private static   HttpHeaders convertRequestHeaderToHttpHeader(RequestTemplate request){
        HttpHeaders httpHeaders=new HttpHeaders();
        Map<String,Collection<String>> headerMap= request.headers();
        headerMap.forEach((k,v)-> httpHeaders.addAll(k,new ArrayList<>(v)));
        return  httpHeaders;
    }

    private static   Map<String, Collection<String>>  convertHttpHeaderToMap(HttpHeaders request){
        Map<String,Collection<String>> headerMap=new HashMap<>();
        Iterator<Map.Entry<String, List<String>>> iterator= request.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<String>> entry=iterator.next();
            headerMap.put(entry.getKey(),entry.getValue());
        }
        return  headerMap;
    }


    private  static   Map<String,Object>   getParsedChainMap(Map<String, Object> requestBodyMap){
        Map<String,Object> map=DFS(requestBodyMap,"",0,new HashMap<>());
        return map;
    }

    /**
     *
     * @param requestMap
     * @param queryStr
     * @param deep
     * @param parsedMap
     * @returnM  key is concat the path of real value in situation which is fetching the value of object java type
     */
    private static Map<String,Object> DFS(Map<String, Object> requestMap,
                                          String queryStr,
                                          int deep,
                                          Map<String,Object> parsedMap){
        if(CollectionUtils.isEmpty(requestMap)){
            return parsedMap;
        }
        for (Map.Entry<String,Object> entry : requestMap.entrySet()) {
            if(entry.getValue() instanceof Map){
                if(deep>=10){
                    throw new RuntimeException("The object has defined so complex,keep simply");
                }
                if("".equals(queryStr)||queryStr==null){
                    DFS((Map<String, Object>)entry.getValue(),entry.getKey(),
                            deep+1,parsedMap);
                }
                else {
                    DFS((Map<String, Object>) entry.getValue(),
                            queryStr + "." + entry.getKey(),
                            deep + 1,
                            parsedMap);
                }
            }
            else {
                if("".equals(queryStr)||null==queryStr){
                    parsedMap.put(entry.getKey(),entry.getValue());
                }
                else {
                    parsedMap.put(queryStr+"."+entry.getKey(),entry.getValue());
                }
                deep=0;

            }

        }
        return parsedMap;

    }



}

