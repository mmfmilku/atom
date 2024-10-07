package org.mmfmilku.atom.transport.frpc;

import org.mmfmilku.atom.transport.handle.FRPCHandle;
import org.mmfmilku.atom.transport.protocol.file.FServer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FRPCStarter {

    private static final String F_SERVER_DIR = System.getProperty("user.dir") + File.separator + "fserver";

    private String scanPackage;
    private List<Class<?>> classes = new ArrayList<>();

    public FRPCStarter(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public void runServer() {
        scanService();
        runWithThread();
    }

    private void runWithThread() {
        File baseDir = new File(F_SERVER_DIR);
        baseDir.delete();
        Thread thread = new Thread("frpc-thread") {
            @Override
            public void run() {
                FServer fServer = new FServer(F_SERVER_DIR).addHandle(new FRPCHandle());
                fServer.start();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    private void scanService() {
        if (scanPackage == null) {
            throw new RuntimeException("错误的frpc扫描路径：" + scanPackage);
        }

        URL resource = Thread.currentThread().getContextClassLoader()
                .getResource(scanPackage.replace(".", "/"));
        if (resource == null) {
            throw new RuntimeException("错误的frpc扫描路径：" + scanPackage);
        }

        File scanDir = new File(resource.getFile());
        scanDir(scanPackage, scanDir);
    }

    private void scanDir(String basePath, File scanFile) {
        if (scanFile.exists()) {
            if (scanFile.isDirectory()) {
                File[] files = scanFile.listFiles();
                if (files == null) {
                    return;
                }
                // 递归获取，传递包名
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanDir(basePath + "." + file.getName(), file);
                    } else {
                        if (file.getName().endsWith(".class")) {
                            String className = basePath + "." +
                                    file.getName().replace(".class", "");
                            try {
                                Class<?> clazz = Class.forName(className);
                                FRpc fRpc = clazz.getAnnotation(FRpc.class);
                                if (fRpc != null) {
                                    classes.add(clazz);
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

}
