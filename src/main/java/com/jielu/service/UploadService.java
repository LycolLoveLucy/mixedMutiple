package com.jielu.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UploadService {

      void upload(List<MultipartFile> multipartFiles) throws RuntimeException, IOException;
}
