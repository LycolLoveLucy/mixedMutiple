package com.jielu.util;

import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

public class MD5EncodeInputStreamUtil {

    private MD5EncodeInputStreamUtil(){}

    public static   String getEncodeInputStream(InputStream inputStream) throws IOException {

        return DigestUtils.md5DigestAsHex(inputStream);
    }
}
