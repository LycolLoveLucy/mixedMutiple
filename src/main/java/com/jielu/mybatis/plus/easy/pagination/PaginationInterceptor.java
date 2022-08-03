package com.jielu.mybatis.plus.easy.pagination;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PaginationInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MetaObject metaObject = SystemMetaObject.forObject(invocation.getTarget());
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
            BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
            Object castedObject = getPageParam(boundSql);
            if (null == castedObject) {
                return invocation.proceed();
            }
            Page page = (Page) castedObject;
            String originalSql = boundSql.getSql();
            Connection connection = (Connection) invocation.getArgs()[0];
            DbType dbType = JdbcUtils.getDbType(connection.getMetaData().getURL());
            String pageSql = DialectBuilder.pageAbleForSelect(originalSql, page, dbType);
            metaObject.setValue("delegate.boundSql.sql", pageSql);
            //查询总条数
            Statement statement = connection.createStatement();
            //java 8+ try Resource can auto close the stream resources
            try (ResultSet resultSet = statement.executeQuery(String.format("select count(1) from  (%s)", originalSql))) {
                if (resultSet.next()) {
                    Long total = resultSet.getLong(1);
                    page.setTotal(total);
                }
            }

        }
        return invocation.proceed();
    }

    private Page getPageParam(BoundSql boundSql) {
        Object paramObject = boundSql.getParameterObject();
        if (paramObject instanceof MapperMethod.ParamMap || paramObject instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) paramObject;
            Set keySet = paramMap.keySet();
            Iterator it = keySet.iterator();
            while (it.hasNext()) {
                if (paramMap.get(it.next()) instanceof Page) {
                    return (Page) paramMap.get(it.next());

                }
            }
        }
        if (boundSql.getParameterObject() instanceof Page) {
            return (Page) boundSql.getParameterObject();
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        //do nothing
    }
}