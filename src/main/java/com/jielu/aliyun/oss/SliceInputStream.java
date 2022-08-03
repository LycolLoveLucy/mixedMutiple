package com.jielu.aliyun.oss;

import com.aliyun.oss.model.UploadPartRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SliceInputStream {

    private final int partSize;
    private InputStream inputStream;

    public SliceInputStream(InputStream inputStream,int partSize) {
        this.inputStream = inputStream;
        this.partSize=partSize;
    }

    public List<UploadPartRequest> collectUploadPartRequestList(String bucketName, String objectName, String uploadId
                                                            ) throws IOException {

        final long partSize = this.partSize;
        int fileLength = inputStream.available();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {
            partCount++;
        }
        List<UploadPartRequest> uploadPartRequestList = new ArrayList<>();
        for (int i = 0; i < partCount; i++) {
            long startPos = i * partSize;
            long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
            //skip position
            inputStream.skip(startPos);
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(objectName);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(inputStream);
            uploadPartRequest.setPartSize(curPartSize);
            uploadPartRequestList.add(uploadPartRequest);
        }
        return uploadPartRequestList;

    }
}
