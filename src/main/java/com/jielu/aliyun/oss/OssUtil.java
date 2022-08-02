package com.jielu.aliyun.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
public class OssUtil {

    /**
     * get the result url this url can be used for download
     * @param multipartFileList
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public List<String> uploadFileToOss(List<MultipartFile> multipartFileList){
        String endPoint = "", accessKeyId = "", accessKeySecret = "", bucket = "", bucketName = "";
        String objectName = "/lycol/upload/oss/"
                + FastDateFormat.getInstance("yyyyMMdd", Locale.CHINESE).format(new Date());


        OSSClient ossClient = OssClientFactory.createOssClient(endPoint, accessKeyId, accessKeySecret, bucket);

        // 初始化分片。
        List<String> res = new ArrayList<>();
        try {

        for (MultipartFile multipartFile : multipartFileList) {
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName,
                    objectName+getFileName(multipartFile));
            InitiateMultipartUploadResult uploadResult = ossClient.initiateMultipartUpload(request);
            String uploadId = uploadResult.getUploadId();

            InputStream inputStream = multipartFile.getInputStream();
            SliceInputStream sliceInputStream = new SliceInputStream(inputStream, 1024);
            List<UploadPartRequest> uploadPartRequestList = sliceInputStream.collectUploadPartRequestList(bucketName, objectName, uploadId);
            OssMultipleThreadUploadExecutor ossMultipleThreadUploadExecutor =
                    new OssMultipleThreadUploadExecutor(ossClient);

            List<PartETag> partETags = ossMultipleThreadUploadExecutor.genPartTag(uploadPartRequestList);
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);

            ossClient.completeMultipartUpload(completeMultipartUploadRequest);
            res.add(endPoint+ File.separator+objectName);
            IOUtils.close(inputStream);
        }
        }

        catch (Exception e){
            throw  new RuntimeException(e);
        }
        finally {
            ossClient.shutdown();
        }

        return res;

    }

    private String getFileName(MultipartFile file) {
        String filename = file.getOriginalFilename();
        int lastIndexOf = filename.lastIndexOf(".");
        String suffix = filename.substring(lastIndexOf);
        return suffix;
    }
}
