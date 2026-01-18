package org.mmfmilku.atom.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 工具类代码冗余写一遍，为了不与agent-core依赖的类重复，因为此工具类在agent入口类中调用，与atom-core使用的不同的类加载器
 **/
public class LoaderUtil {

    public static String getStartClass() {
        String command = System.getProperty("sun.java.command");
        if (isEmpty(command) || command.contains(".jar")) {
            String path = System.getProperty("java.class.path");
            try {
                if (isEmpty(path)) {
                    throw new IllegalStateException("Unable to determine code source archive");
                } else {
                    File root = new File(path);
                    if (!root.exists()) {
                        String path2 = Thread.currentThread()
                                .getContextClassLoader().getResource(path).getFile();
                        root = new File(path2);
                    }
                    if (!root.exists()) {
                        return "input_your_app_base_path";
                    } else {
                        JarFile jarFile = new JarFile(root);
                        Manifest manifest = jarFile.getManifest();
                        System.out.println(manifest);
                        Attributes mainAttributes = manifest.getMainAttributes();
                        String startClass = mainAttributes.getValue("Start-Class");
                        if (startClass != null) {
                            // springboot 框架
                            return startClass;
                        }
                        return mainAttributes.getValue("Main-Class");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("getStartClass fail");
            }

        }
        String[] s = command.split(" ");
        return s[0];
    }

    public static Class<?> searchClass(Instrumentation inst, String searchClassName) {
        System.out.println("searchClass for:" + searchClassName);
        Class<?>[] loadedClasses = inst.getAllLoadedClasses();
        for (Class<?> loadedClass : loadedClasses) {
            if (loadedClass.getName().equals(searchClassName)) {
                System.out.println("searchClass for:" + searchClassName + " get " + loadedClass);
                return loadedClass;
            }
        }
        System.out.println("searchClass for:" + searchClassName + " get null");
        return null;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}
