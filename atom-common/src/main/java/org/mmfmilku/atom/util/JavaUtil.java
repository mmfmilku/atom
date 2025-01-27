package org.mmfmilku.atom.util;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JavaUtil {

    public static String getStartClass() {
        String command = System.getProperty("sun.java.command");
        if (StringUtils.isEmpty(command) || command.contains(".jar")) {
            String path = System.getProperty("java.class.path");
            try {
                if (StringUtils.isEmpty(path)) {
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
        return command;
    }

}
