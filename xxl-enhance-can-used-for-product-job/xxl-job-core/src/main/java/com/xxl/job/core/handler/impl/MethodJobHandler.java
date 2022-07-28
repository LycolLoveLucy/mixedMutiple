package com.xxl.job.core.handler.impl;

import com.xxl.job.core.handler.IJobHandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxueli 2019-12-11 21:12:18
 */
public class MethodJobHandler extends IJobHandler {

    private final Object target;
    private final Method method;
    private Method initMethod;
    private Method destroyMethod;


    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new HashMap<>(16);

    static {
        PRIMITIVE_WRAPPER_TYPE_MAP.put(boolean.class, Boolean.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(byte.class, Byte.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(char.class, Character.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(double.class, Double.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(float.class, Float.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(int.class, Integer.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(long.class, Long.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(short.class, Short.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(boolean[].class, Boolean[].class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(byte[].class, Byte[].class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(char[].class, Character[].class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(double[].class, Double[].class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(float[].class, Float[].class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(int[].class, Integer[].class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(long[].class, Long[].class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(short[].class, Short[].class);

    }

    public MethodJobHandler(Object target, Method method, Method initMethod, Method destroyMethod) {
        this.target = target;
        this.method = method;

        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    @Override
    public void execute() throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();
        //// method-param can  be primitive-types
        for(int i=1;i<paramTypes.length;i++){
          if(PRIMITIVE_WRAPPER_TYPE_MAP.containsKey(paramTypes[i])){
              paramTypes[i]=PRIMITIVE_WRAPPER_TYPE_MAP.get(paramTypes[i]);
            }
        }
        method.setAccessible(true);
        if (paramTypes.length > 0) {
            method.invoke(target, new Object[paramTypes.length]);
        } else {
            method.invoke(target);
        }
    }

    @Override
    public void init() throws Exception {
        if(initMethod != null) {
            initMethod.invoke(target);
        }
    }

    @Override
    public void destroy() throws Exception {
        if(destroyMethod != null) {
            destroyMethod.invoke(target);
        }
    }


    @Override
    public String toString() {
        return super.toString()+"["+ target.getClass() + "#" + method.getName() +"]";
    }
}
