package org.mmfmilku.atom.transport.frpc;

import org.mmfmilku.atom.transport.handle.FRPCHandle;
import org.mmfmilku.atom.transport.protocol.file.FServer;
import org.mmfmilku.atom.util.IOUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FRPCStarter {

    private static final String F_SERVER_DIR = System.getProperty("user.dir") + File.separator + "fserver";

    private String scanPackage;
    private List<Class<?>> classes = new ArrayList<>();
    private Map<String, ServiceMapping> mappings = new HashMap<>();

    public FRPCStarter(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public void runServer() {
        scanService();
        mapService();
        runWithThread();
    }

    private void runWithThread() {
        File baseDir = new File(F_SERVER_DIR);
        baseDir.delete();
        Thread thread = new Thread("frpc-main-thread") {
            @Override
            public void run() {
                FServer fServer = new FServer(F_SERVER_DIR).addHandle(new FRPCHandle(mappings));
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
                                FRPCService annotation = clazz.getAnnotation(FRPCService.class);
                                if (annotation != null) {
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

    private void mapService() {
        if (classes.size() == 0) {
            return;
        }
        try {
            for (Class<?> clazz : classes) {
                Object invokeObj = clazz.newInstance();
                ServiceMapping serviceMapping = new ServiceMapping();
                serviceMapping.setInvokeObj(invokeObj);
                Map<String, Function<FRPCParam, FRPCReturn>> funcMap = new HashMap<>();
                serviceMapping.setFuncMap(funcMap);

                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    FRPCService annotation = method.getAnnotation(FRPCService.class);
                    method.setAccessible(true);
                    if (annotation != null) {
                        Function<FRPCParam, FRPCReturn> callFunc = param -> {
                            try {
                                Object returnData = method.invoke(invokeObj, (Object[]) param.getData());
                                FRPCReturn frpcReturn = new FRPCReturn();
                                frpcReturn.setData(returnData);
                                return frpcReturn;
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        };
                        // TODO 不支持重载
                        funcMap.put(method.getName(), callFunc);
                    }
                }

                mappings.put(clazz.getName() ,serviceMapping);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Function<Object, byte[]> SERIALIZE_FUNC = IOUtils::serialize;

    private static Function<byte[], Object> DESERIALIZE_FUNC = IOUtils::deserialize;

}
