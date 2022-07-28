package com.xxl.job.core.util;

import com.xxl.job.core.biz.model.HandleCallbackParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xuxueli 2020-04-12 0:14:00
 */
public class JdkSerializeWithZipTool {
    private static Logger logger = LoggerFactory.getLogger(JdkSerializeWithZipTool.class);


    // ------------------------ serialize and unserialize ------------------------

    /**
     * 将对象-->byte[] (由于jedis中不支持直接存储object所以转换成byte[]存入)
     *
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
           return GZipUtils.compress(bytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtil.close(baos,oos);
        }
        return null;
    }


    /**
     * 将byte[] -->Object
     *
     * @param bytes
     * @return
     */
    public static  <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bytes=GZipUtils.decompress(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
               bais.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        List< HandleCallbackParam > callbackParamList=new ArrayList<>();
        HandleCallbackParam handleCallbackParam=new HandleCallbackParam();
        handleCallbackParam.setHandleCode(1);
        handleCallbackParam.setHandleMsg("false");
        callbackParamList.add(handleCallbackParam);
       byte[]data=  serialize(callbackParamList);
        callbackParamList= (List<HandleCallbackParam>) deserialize(data,List.class);
        System.out.println(callbackParamList.get(0).getHandleCode());
    }

}
