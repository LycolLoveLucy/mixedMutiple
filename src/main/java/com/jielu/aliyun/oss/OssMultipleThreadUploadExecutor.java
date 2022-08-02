package com.jielu.aliyun.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.jielu.leetcode.NamedThreadFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * OSS Mul-Thread upload file
 */
public class OssMultipleThreadUploadExecutor {

    private  final OSSClient ossClient;

    public    OssMultipleThreadUploadExecutor( OSSClient ossClient){
        this.ossClient=ossClient;
    }

   final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
           10,
           50,
           1000 * 60,
           TimeUnit.MILLISECONDS,
           new LinkedBlockingQueue<>(),
           new NamedThreadFactory("thread-oss-upload-file"),
           (r, executor) -> r.run()
   );


     public List<PartETag> genPartTag(List<UploadPartRequest> uploadPartResultList) throws InterruptedException {

         CountDownLatch countDownLatch = new CountDownLatch(uploadPartResultList.size());
         List<PartETag> partETags=new ArrayList<>(uploadPartResultList.size());
         for (int i = 1; i <uploadPartResultList.size(); i++) {
             int finalI = i;
             threadPoolExecutor.execute(() -> {
                 try {
                     UploadPartResult  uploadPartResult= ossClient.uploadPart(uploadPartResultList.get(finalI));
                     partETags.add(uploadPartResult.getPartETag());
                 } catch (Throwable e) {
                     throw new RuntimeException(e);
                 }
             });
             countDownLatch.countDown();;
         }
         countDownLatch.await();
         threadPoolExecutor.shutdown();
         if(threadPoolExecutor.isTerminated()){
             threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
         }

         Collections.sort(partETags, Comparator.comparingInt(PartETag::getPartNumber));
         return partETags;

     }



}
