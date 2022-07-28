package com.xxl.job.core.server;

import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.IdleBeatParam;
import com.xxl.job.core.biz.model.KillParam;
import com.xxl.job.core.biz.model.LogParam;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.util.GsonTool;

import java.util.HashMap;
import java.util.Map;

public class NettyHttpStrategyExeByUri {

    ExecutorBiz executorBiz;


    public NettyHttpStrategyExeByUri(ExecutorBiz executorBiz){
        this.executorBiz=executorBiz;
    }


    public Map<String,Executor> getExecutorMapping(){

        Map<String,Executor> executorMap=new HashMap<>();
        executorMap.put("/beat", requestData -> executorBiz.beat());

        executorMap.put("/idleBeat", requestData -> {
            IdleBeatParam idleBeatParam = GsonTool.fromJson(requestData, IdleBeatParam.class);
            return executorBiz.idleBeat(idleBeatParam);
        });

        executorMap.put("/run", requestData -> {
            TriggerParam triggerParam = GsonTool.fromJson(requestData, TriggerParam.class);
            return executorBiz.run(triggerParam);
        });

        executorMap.put("/kill", requestData -> {
            LogParam logParam = GsonTool.fromJson(requestData, LogParam.class);
            return executorBiz.log(logParam);
        });


        executorMap.put("/log", requestData -> {
            KillParam killParam = GsonTool.fromJson(requestData, KillParam.class);
            return executorBiz.kill(killParam);
        });

        return  executorMap;

    }


}
