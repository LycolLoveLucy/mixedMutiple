package com.jielu.aliyun.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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
    public List<String> uploadFileToOss(List<MultipartFile> multipartFileList) throws IOException, InterruptedException {
        String endPoint = "", accessKeyId = "", accessKeySecret = "", bucket = "", bucketName = "";
        String objectName = "/lycol/upload/oss/" + FastDateFormat.getInstance("yyyyMMdd", Locale.CHINESE).format(new Date());

        OSSClient ossClient = OssClientFactory.createOssClient(endPoint, accessKeyId, accessKeySecret, bucket);
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName);
        // 初始化分片。
        InitiateMultipartUploadResult uploadResult = ossClient.initiateMultipartUpload(request);
        String uploadId = uploadResult.getUploadId();
        List<String> res = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFileList) {

            InputStream inputStream = multipartFile.getInputStream();
            SliceInputStream sliceInputStream = new SliceInputStream(inputStream, 1024);

            List<UploadPartRequest> uploadPartRequestList = sliceInputStream.genUploadPartRequestList(bucketName, objectName, uploadId);
            OssMultipleThreadUploadExecutor ossMultipleThreadUploadExecutor =
                    new OssMultipleThreadUploadExecutor(ossClient);

            List<PartETag> partETags = ossMultipleThreadUploadExecutor.genPartTag(uploadPartRequestList);
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);

            CompleteMultipartUploadResult completeMultipartUploadResult =
                    ossClient.completeMultipartUpload(completeMultipartUploadRequest);

            res.add(completeMultipartUploadResult.getLocation());
        }
        ossClient.shutdown();
        return res;

    }


}
