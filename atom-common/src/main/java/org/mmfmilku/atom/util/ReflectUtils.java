package org.mmfmilku.atom.util;

import org.mmfmilku.atom.consts.CodeConst;
import org.mmfmilku.atom.exception.BizException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

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
        Method method = getMethod(clazz, invokeMethod, params);
        return method.invoke(invokeObj, params);
    }

    /**
     * 调用静态方法
     * */
    public static Object invokeStaticMethod(Class<?> clazz, String invokeMethod, Object ...params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getMethod(clazz, invokeMethod, params);
        return method.invoke(null, params);
    }

    private static Method getMethod(Class<?> clazz, String invokeMethod, Object[] params) throws NoSuchMethodException {
        Class<?>[] paramsType = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            paramsType[i] = param.getClass();
        }
        Method method = clazz.getDeclaredMethod(invokeMethod, paramsType);
        method.setAccessible(true);
        return method;
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

    public static List<String> scanClass(String scanPackage) {
        // TODO，仅扫描了当前线程所在class路径
        URL resource = Thread.currentThread().getContextClassLoader()
                .getResource(scanPackage.replace(".", "/"));
        if (resource == null) {
            throw new RuntimeException("错误的扫描路径：" + scanPackage);
        }

        List<String> scanClassList = new ArrayList<>();

        String protocol = resource.getProtocol();
        if ("jar".equals(protocol)) {
            try {
                JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();
                Enumeration<JarEntry> entries = jarURLConnection.getJarFile().entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().endsWith(CodeConst.CLASS_FILE_SUFFIX)) {
                        String scanClassName = CodeUtils.toClassName(jarEntry.getName());
                        if (scanClassName.startsWith(scanPackage)) {
                            scanClassList.add(CodeUtils.toClassName(jarEntry.getName()));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            File scanDir = new File(resource.getFile());
            scanDir(scanPackage, scanDir, scanClassList);
            if (scanDir.getAbsolutePath().contains("test-classes")) {
                // 单元测试
                scanDir(scanPackage,
                        new File(resource.getFile().replace("test-classes", "classes")),
                        scanClassList);
            }
        }

        return scanClassList;
    }

    private static void scanDir(String basePath, File scanFile, List<String> scanClassList) {
        if (scanFile.exists()) {
            if (scanFile.isDirectory()) {
                File[] files = scanFile.listFiles();
                if (files == null) {
                    return;
                }
                // 递归获取，传递包名
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanDir(basePath + "." + file.getName(), file, scanClassList);
                    } else {
                        if (file.getName().endsWith(CodeConst.CLASS_FILE_SUFFIX)) {
                            String className = basePath + "." +
                                    file.getName().replace(CodeConst.CLASS_FILE_SUFFIX, "");
                            scanClassList.add(className);
                        }
                    }
                }
            }
        }
    }

}
