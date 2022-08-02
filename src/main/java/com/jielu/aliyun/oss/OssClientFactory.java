package com.jielu.aliyun.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;

public class OssClientFactory {

  public static   OSSClient createOssClient(String endpoint,
                                            String accessKeyId,
                                            String accessKeySecret,
                                            String bucket) {

      OSSClientBuilder ossClientBuilder=new OSSClientBuilder();
      // 创建OSSClient实例。
      OSSClient ossClient = (OSSClient) ossClientBuilder.build(endpoint, accessKeyId, accessKeySecret);
      ossClient.createBucket(bucket);
      return  ossClient;
  }
}
