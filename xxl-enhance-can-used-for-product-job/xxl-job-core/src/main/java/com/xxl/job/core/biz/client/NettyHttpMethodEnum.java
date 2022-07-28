package com.xxl.job.core.biz.client;

public enum NettyHttpMethodEnum {

    beat("beat"),
    idlebeat("idleBeat"),
    run("run"),
    kill("kill"),
    log("log");

    private  String httpMethodName;



    NettyHttpMethodEnum(String httpMethodName) {
        this.httpMethodName = httpMethodName;
    }

    public String getHttpMethodName() {
        return httpMethodName;
    }
}
