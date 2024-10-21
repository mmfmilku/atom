package org.mmfmilku.atom.util;

import org.mmfmilku.atom.exception.BizException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtils {

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 调用成员方法
     * */
    public static Object invokeMethod(Object invokeObj, String invokeMethod, Object ...params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = invokeObj.getClass();
        Class<?>[] paramsType = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            paramsType[i] = param.getClass();
        }
        Method method = clazz.getDeclaredMethod(invokeMethod, paramsType);
        method.setAccessible(true);
        return method.invoke(invokeObj, params);
    }

    /**
     * 获取成员变量
     * */
    public static Object getMember(Object invokeObj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = invokeObj.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(invokeObj);
    }

}
