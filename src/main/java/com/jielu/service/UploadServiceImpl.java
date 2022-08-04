package com.jielu.service;

import com.jielu.cache.localcache.JVMLocalCache;
import com.jielu.util.MD5EncodeInputStreamUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
public class UploadServiceImpl  implements  UploadService{

    @Resource
    JVMLocalCache jvmLocalCache;

   // @RetryFlag(triggerExceptionClass =RuntimeException.class,number=3)
    @Override
    public void upload(List<MultipartFile> multipartFiles) throws RuntimeException, IOException {

        for(MultipartFile multipartFile:multipartFiles){
            String md5Str= MD5EncodeInputStreamUtil.getEncodeInputStream(multipartFile.getInputStream());
            if(jvmLocalCache.get(md5Str)!=null){
                throw  new RuntimeException("The current file has been uploaded before action,repeat md5 key is:"+md5Str);
            }
            jvmLocalCache.put(md5Str,multipartFile);
        }

    }
}
