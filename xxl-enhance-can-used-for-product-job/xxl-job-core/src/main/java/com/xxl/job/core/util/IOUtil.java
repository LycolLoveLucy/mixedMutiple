package com.xxl.job.core.util;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {



    public static void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException("Closeable ");
            }
        }
    }


    public static void close(final Closeable... closeable) {
       for(Closeable c:closeable){
           close(c);
       }
    }
}
