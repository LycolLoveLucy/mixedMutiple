package com.jielu.service;

import com.jielu.cache.localcache.JVMLocalCache;
import com.jielu.util.MD5EncodeInputStreamUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UploadServiceImpl  implements  UploadService{

    static  final JVMLocalCache localCache=JVMLocalCache.getInstance(60*60);

    @Override
    public void upload(List<MultipartFile> multipartFiles) throws RuntimeException, IOException {

        for(MultipartFile multipartFile:multipartFiles){
            String md5Str= MD5EncodeInputStreamUtil.getEncodeInputStream(multipartFile.getInputStream());
            if(localCache.get(md5Str)!=null){
                throw  new RuntimeException("文件不允许重复上传,repeat md5 key is:"+md5Str);
            }
            localCache.put(md5Str,multipartFile);
        }

    }
}
