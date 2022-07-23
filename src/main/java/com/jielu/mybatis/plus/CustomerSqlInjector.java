package com.jielu.mybatis.plus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;

import java.util.List;

/**
 * 添加Sql注入方法,支持空字段更新
 */
public class CustomerSqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass){
        List<AbstractMethod> methodList=super.getMethodList(mapperClass);
        methodList.add(new BatchInsertByList());
        return methodList;
    }
}
