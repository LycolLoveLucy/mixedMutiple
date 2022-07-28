package com.xxl.job.core.server;

import com.xxl.job.core.biz.model.ReturnT;

public   interface Executor{

    ReturnT<?> exec(String requestData);

    }