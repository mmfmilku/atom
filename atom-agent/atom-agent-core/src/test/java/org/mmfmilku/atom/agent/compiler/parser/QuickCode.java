package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.util.JavaUtil;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class QuickCode {

    private String test() {
        String command = System.getProperty("sun.java.command");
        if (command.endsWith(".jar")) {
            ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URI location = null;
            try {
                String path;
                path = codeSource.getLocation().toURI().getSchemeSpecificPart();
                if (path == null) {
                    throw new IllegalStateException("Unable to determine code source archive");
                }
                File root = new File(path = path.substring(0, path.indexOf("!")));
                if (!root.exists()) {
                    String path2 = Thread.currentThread().getContextClassLoader().getResource(path).getFile();
                    root = new File(path2);
                }
                if (!root.exists()) {
                    throw new IllegalStateException("Unable to determine code source archive from " + root);
                }
                JarFile jarFile = new JarFile(root);
                Manifest manifest = jarFile.getManifest();
                System.out.println(manifest);
                String startClass = manifest.getMainAttributes().getValue("Start-Class");
                String mainClass = manifest.getMainAttributes().getValue("Main-Class");
                return startClass;
            }
            catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                return "error";
            }
        }
        return command;
    }

}
