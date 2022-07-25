package com.jielu.mybatis.plus;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author:lycol
 * Add batch insert custom method when using mybatis-plus
 * This is not reuse sqlSession but  real sql-level by insert schema
 */
public class BatchInsertByList extends AbstractMethod {
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = new NoKeyGenerator();

        SqlSource sqlSource = languageDriver.createSqlSource(configuration,
                getBatchInsertSql(tableInfo,modelClass),
                Collections.class);

        return this.addMappedStatement(
                mapperClass,
                CustomSqlMethod.INSERT_BATCH.getMethod(),
                sqlSource,
                SqlCommandType.INSERT,
                Collections.class,
                null,
                int.class,
                keyGenerator,
                null,
                null
        );

    }

    private String getBatchInsertSql(TableInfo tableInfo, Class<?> modelClass){
        if(!modelClass.isAnnotationPresent(Dialect.class)){
            throw  new RuntimeException("The model :"+modelClass.getName()+" must not be no annotation Dialect");
        }
        String batchInsertSql=CustomSqlMethod.INSERT_BATCH.getSql();
        StringBuilder insertColumnBuilder=new StringBuilder();
        StringBuilder itemColumnBuilder=new StringBuilder();
        List<TableFieldInfo> fieldList=tableInfo.getFieldList();
        int size=fieldList.size();
        Field[]fields= modelClass.getDeclaredFields();

        //ID Field
        String idField="";
        //Add primary column attribute
        idField = getInsertColumnString(insertColumnBuilder, fields, idField);
        //获取父类的属性字段
        if(ObjectUtils.isEmpty(idField)) {
            Field[] superFields = modelClass.getSuperclass().getDeclaredFields();
            //添加主键column
            for (Field f : superFields) {
                if (f.isAnnotationPresent(TableId.class)) {
                    TableId tableId = f.getAnnotation(TableId.class);
                    insertColumnBuilder.append(tableId.value()).append(",");
                    idField = f.getName();
                }
            }
        }

        for(int i=0;i<size-1;i++){
            TableFieldInfo tableFieldInfo=fieldList.get(i);
            insertColumnBuilder.append(tableFieldInfo.getColumn()).append(",");
            String jdbcType=getJdbcTypeByClassType(tableFieldInfo.getPropertyType());
            itemColumnBuilder.append("#{item." + tableFieldInfo.getProperty() + ",jdbcType="+jdbcType+"},\n");
        }
        TableFieldInfo tableFieldInfo=fieldList.get(size-1);
        insertColumnBuilder.append(tableFieldInfo.getColumn());

        String jdbcType=getJdbcTypeByClassType(tableFieldInfo.getPropertyType());
        itemColumnBuilder.append("#{item." + tableFieldInfo.getProperty() + ",jdbcType="+jdbcType+"}");

        String foreachSql;
        //如果是oracle数据库
        Dialect dialect= modelClass.getAnnotation(Dialect.class);
        if(dialect.type()== Type.ORACLE) {
            foreachSql = getForeachOracleSql(itemColumnBuilder);
        }
        //如果是非oracle数据库
       else   {
            foreachSql = getForeachMySQLSql();
            foreachSql=String.format(foreachSql,"#{item."+idField+"}",itemColumnBuilder);
        }
        return  String.format(batchInsertSql,tableInfo.getTableName(),insertColumnBuilder,foreachSql);
    }

    private String getInsertColumnString(StringBuilder insertColumnBuilder, Field[] fields, String idField) {
        for(Field f: fields){
            if(f.isAnnotationPresent(TableId.class)){
                TableId tableId=f.getAnnotation(TableId.class);
                insertColumnBuilder.append(tableId.value()).append(",");
                idField =f.getName();
                break;
            }
            //else situation is the no @TableId annotation from the model-classes
            else{
                for(char c:f.getName().toCharArray()){
                    if(Character.isUpperCase(c)){
                        insertColumnBuilder.append("_").append(c);
                    }
                    else {
                        insertColumnBuilder.append(c);
                    }
                }
            }
        }
        return idField;
    }

    private String getJdbcTypeByClassType(Class clazz){
        if(clazz.getSuperclass()==Number.class){
            return JdbcType.NUMERIC.name();
        }
        if(clazz==String.class){
            return  JdbcType.VARCHAR.name();
        }
        if(clazz== Date.class||clazz== java.sql.Date.class){
            return  JdbcType.DATE.name();
        }
        //default is java_object type
        return  JdbcType.JAVA_OBJECT.name();

    }

    private String getForeachMySQLSql() {
        return "VALUES" +
                " <foreach collection=\"items\" item='item'  open='' index='index' separator=','>\n" +
                "(%s,%s)</foreach>";
    }

    private String getForeachOracleSql(StringBuilder itemColumnBuilder) {
        return "SELECT RAWTOHEX(SYS_GUID()), record.* FROM (\n" +
                " <foreach collection=\"items\" "
                + "item='item' "
                + "index='index' "
                + "separator=\"union all\">\n"
                + " select\n" + itemColumnBuilder
                + " FROM dual\n" + " </foreach>"
                + " ) record";
    }
}