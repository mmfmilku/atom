package org.mmfmilku.atom.agent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 用于桥接用户类对agent核心类的调用
 **/
public class AgentBridge {

    public static void get() {
        ClassLoader atomClassLoader = LoaderBootstrap.atomClassLoader;
    }

    public static void print(String str) throws ClassNotFoundException {
        ClassLoader atomClassLoader = LoaderBootstrap.atomClassLoader;
        Class<?> clazz = atomClassLoader.loadClass("org.mmfmilku.atom.agent.log.ScreenLogger");
        Method printMethod = null;
        try {
            printMethod = clazz.getMethod("print", String.class);
            printMethod.invoke(null, str);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
