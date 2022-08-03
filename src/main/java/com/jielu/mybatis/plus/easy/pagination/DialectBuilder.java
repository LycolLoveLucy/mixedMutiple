package com.jielu.mybatis.plus.easy.pagination;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * Created by Administrator on 2019/8/11.
 */
public class DialectBuilder {

    public static String pageAbleForSelect(String originalSql, Page page, DbType dbType) {
       return PageSqlEnumDialect.getInitPageSql(dbType,page.getCurrent(),page.getSize(),originalSql);
    }
}