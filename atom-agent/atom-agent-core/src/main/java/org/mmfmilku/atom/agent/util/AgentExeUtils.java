package org.mmfmilku.atom.agent.util;

import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.log.ScreenLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 文件描述
 **/
public class AgentExeUtils {

    public static String toString(Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof String) {
            return (String) o;
        }
        String toStringLongName = AgentProperties.toStringFullName();
        if (toStringLongName == null || toStringLongName.trim().isEmpty()) {
            return o.toString();
        }
        int i = toStringLongName.lastIndexOf(".");
        String toStringClass = toStringLongName.substring(0, i);
        String toStringMethod = toStringLongName.substring(i + 1);
        try {
            Class<?> toStringClazz = InstrumentationContext.searchClass(toStringClass);
            if (toStringClazz == null) {
                ScreenLogger.warn("找不到toString方法配置中的类:" + toStringClass);
                return o.toString();
            }
            Method method = toStringClazz.getMethod(toStringMethod, Object.class);
            Object toStringData = method.invoke(null, o);
            return toStringData.toString();
        } catch (NullPointerException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            ScreenLogger.error(e, "获取toString方法配置中的方法失败:" + toStringMethod);
            return o.toString();
        }
    }

}
