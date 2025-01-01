package org.mmfmilku.atom.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JavaUtil {

    public static String getStartClass() {
        String command = System.getProperty("sun.java.command");
        if (command.contains(".jar")) {
            ProtectionDomain protectionDomain = Thread.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URI location = null;
            try {
                location = codeSource != null ? codeSource.getLocation().toURI() : null;
                String path = location != null ? location.getSchemeSpecificPart() : null;
                if (path == null) {
                    throw new IllegalStateException("Unable to determine code source archive");
                } else {
                    path = path.substring(0, path.indexOf("!"));
                    File root = new File(path);
                    if (!root.exists()) {
                        String path2 = Thread.currentThread()
                                .getContextClassLoader().getResource(path).getFile();
                        root = new File(path2);
                    }
                    if (!root.exists()) {
                        throw new IllegalStateException("Unable to determine code source archive from " + root);
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
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException("getStartClass fail");
            }

        }
        return command;
    }

}
