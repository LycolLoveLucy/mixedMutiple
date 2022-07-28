package com.xxl.job.core.log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileLock {

    private String path;

    public FileLock(String path){
        this.path=path;
    }

    public void lock(){
        Path path = Paths.get(this.path);
        for(;;){

            try {
                //获得锁
                Files.createFile(path);
                break;
            }catch (Exception ex){
                Thread.yield();
            }
        }
    }

    /**
     * 释放锁
     */
    public void unlock(){

        try {
            Files.delete(Paths.get(path));
        }catch (Exception ex){}
    }
}

