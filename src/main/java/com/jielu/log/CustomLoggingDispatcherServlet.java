package com.jielu.log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class CustomLoggingDispatcherServlet extends DispatcherServlet {
    private static final Logger logger = LoggerFactory.getLogger("logging");
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContentCachingRequestWrapper requestWrapper   =   new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper =   new ContentCachingResponseWrapper(response);
        //创建一个 json 对象，用来存放 http 日志信息
        ObjectNode rootNode = getJsonNodes(requestWrapper);
        String method = requestWrapper.getMethod();
        rootNode.put("method", method);
        try {
            super.doDispatch(requestWrapper, responseWrapper);
        }
        finally {
            finallyExecution(request, response, requestWrapper, responseWrapper, rootNode, method);
        }
    }

    private void finallyExecution(HttpServletRequest request, HttpServletResponse response, ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper, ObjectNode rootNode, String method) throws IOException {
        if(Objects.equals(HttpMethod.GET.name(), method)) {
            rootNode.set("request", mapper.valueToTree(requestWrapper.getParameterMap()));
        }
        else {
            if(request.getContentType().contains(MediaType.MULTIPART_FORM_DATA_VALUE)){
                rootNode.set("request", null);
            }
            else {
                JsonNode newNode = mapper.readTree(requestWrapper.getContentAsByteArray());
                rootNode.set("request", newNode);
            }
        }

        if(null!= response.getContentType()&& response.getContentType().equals(MediaType.APPLICATION_JSON.getType())) {
            JsonNode newNode = mapper.readTree(responseWrapper.getContentInputStream());
            rootNode.set("response", newNode);
        }
        else{
            JsonNode newNode = JsonNodeFactory.instance.textNode(new String(responseWrapper.getContentAsByteArray()));
            rootNode.set("response", newNode);
        }
        responseWrapper.copyBodyToResponse();
        rootNode.set("responseHeaders", mapper.valueToTree(getResponseHeaders(responseWrapper)));
        logger.info(rootNode.toString());
    }

    private ObjectNode getJsonNodes(ContentCachingRequestWrapper requestWrapper) {
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("uri", requestWrapper.getRequestURI());
        rootNode.put("clientIp", requestWrapper.getRemoteAddr());
        rootNode.set("requestHeaders", mapper.valueToTree(getRequestHeaders(requestWrapper)));
        rootNode.set("requestCookies", mapper.valueToTree(getRequestCookies(requestWrapper)));
        return rootNode;
    }

    private Map<String, Object> getRequestHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;

    }


    private Map<String, Object> getRequestCookies(HttpServletRequest request) {
        Map<String, Object> headers = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length==0){
            return Collections.EMPTY_MAP;
        }
        for (Cookie cookie : cookies) {
            headers.put(cookie.getName(), cookie.getValue());
        }
        return headers;
    }

    private Map<String, Object> getResponseHeaders(ContentCachingResponseWrapper response) {
        Map<String, Object> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            headers.put(headerName, response.getHeader(headerName));
        }
        return headers;
    }

}

