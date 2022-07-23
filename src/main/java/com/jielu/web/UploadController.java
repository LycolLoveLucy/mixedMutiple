package com.jielu.web;

import com.jielu.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    UploadService uploadService;

    @PostMapping
    public R upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        uploadService.upload(Arrays.asList(multipartFile));
        return R.success(null,"上传成功");
    }
}
