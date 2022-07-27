package com.jielu.mybatis.plus;

import java.io.PrintStream;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;

import com.jielu.util.CollectionUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;


/**
 * Mybatis printing formatted sql,support
 */
@Intercepts({@org.apache.ibatis.plugin.Signature(
        type = org.apache.ibatis.executor.Executor.class, method = SqlMethod.update,
        args = {MappedStatement.class, Object.class}),
        @org.apache.ibatis.plugin.Signature(type = org.apache.ibatis.executor.Executor.class, method = SqlMethod.query,
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @org.apache.ibatis.plugin.Signature(
                type = org.apache.ibatis.executor.Executor.class, method = SqlMethod.insert,
                args = {MappedStatement.class, Object.class}),
})
@Component
public class MybatisResultInterceptor
        implements Interceptor {


    public Object intercept(Invocation invocation)
            throws Throwable {
        try {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = null;
            if (invocation.getArgs().length > 1) {
                parameter = invocation.getArgs()[1];
            }
            String sqlId = mappedStatement.getId();
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            String sql = getSql(configuration, boundSql, sqlId);
            PrintStream printStream = new PrintStream(System.out);
            printStream.println(sql);
        } catch (Exception localException) {

        }
        return invocation.proceed();
    }

    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId) {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder(100);
        str.append(sqlId);
        str.append(":");
        str.append(sql);
        return str.toString();
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if ((obj instanceof String)) {
            value = "'" + obj + "'";
        } else if ((obj instanceof Date) || (obj instanceof LocalDateTime)) {
            DateFormat formatter = DateFormat.getDateTimeInstance(2, 2, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else if (obj != null) {
            value = obj.toString();
        } else {
            value = "";
        }
        return value;
    }

    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        MetaObject metaObject;
        if ((CollectionUtils.isNotEmpty(parameterMappings)) && (parameterObject != null)) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        sql = sql.replaceFirst("\\?", "Missing paramter");
                    }
                }
            }
        }
        return sql;
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }
}