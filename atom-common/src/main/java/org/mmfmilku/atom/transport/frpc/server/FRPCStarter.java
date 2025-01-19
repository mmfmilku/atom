package org.mmfmilku.atom.transport.frpc.server;

import org.mmfmilku.atom.consts.CodeConst;
import org.mmfmilku.atom.transport.protocol.file.FServer;
import org.mmfmilku.atom.util.AssertUtil;
import org.mmfmilku.atom.util.CodeUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarEntry;

public class FRPCStarter {

    public static final String F_SERVER_DIR = System.getProperty("user.dir") + File.separator + "fserver";

    private String scanPackage;
    private String fDir;
    private List<Class<?>> classes = new ArrayList<>();
    private Map<String, ServiceMapping> mappings = new HashMap<>();
    private Listener listener;
    private FServer fServer;

    public FRPCStarter(String scanPackage, String fDir) {
        AssertUtil.notnull(scanPackage, "FRPC服务包路径为空");
        this.scanPackage = scanPackage;
        this.fDir = fDir;
    }

    public void runServer() {
        scanService();
        mapService();
        run();
    }

    public void stopServer() {
        if (fServer != null) {
            fServer.stop();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void run() {
        try {
            fServer = new FServer(fDir)
                    .addHandle(new FRPCHandle(mappings));
            fServer.start();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            if (listener != null) {
                listener.onFail(throwable);
            }
        } finally {
            if (listener != null) {
                listener.onClose();
            }
        }
    }

    private void scanService() {
        if (scanPackage == null) {
            throw new RuntimeException("错误的frpc扫描路径：" + scanPackage);
        }

        // TODO，仅扫描了当前线程所在class路径
        URL resource = Thread.currentThread().getContextClassLoader()
                .getResource(scanPackage.replace(".", "/"));
        if (resource == null) {
            throw new RuntimeException("错误的frpc扫描路径：" + scanPackage);
        }

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
                            registerService(CodeUtils.toClassName(jarEntry.getName()));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            File scanDir = new File(resource.getFile());
            scanDir(scanPackage, scanDir);
            if (scanDir.getAbsolutePath().contains("test-classes")) {
                // 单元测试
                scanDir(scanPackage, new File(resource.getFile().replace("test-classes", "classes")));
            }
        }

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
                        if (file.getName().endsWith(CodeConst.CLASS_FILE_SUFFIX)) {
                            String className = basePath + "." +
                                    file.getName().replace(CodeConst.CLASS_FILE_SUFFIX, "");
                            registerService(className);
                        }
                    }
                }
            }
        }
    }

    private void registerService(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            FRPCService annotation = clazz.getAnnotation(FRPCService.class);
            if (annotation != null) {
                classes.add(clazz);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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
                Class<?>[] interfaces = clazz.getInterfaces();
                if (interfaces.length != 1) {
                    throw new RuntimeException("FRPCService must have only one interface:" + clazz.getName());
                }

                String serviceName = interfaces[0].getName();
                if (mappings.containsKey(serviceName)) {
                    throw new RuntimeException("repeat service impl for " + serviceName
                            + ": " + mappings.get(serviceName).getInvokeObj().getClass().getName()
                            + "," + clazz.getName());
                }

                for (Method method : methods) {
                    method.setAccessible(true);
                    Class<?>[] declaringInterfaces = method.getDeclaringClass().getInterfaces();

                    if (declaringInterfaces.length == 1 &&
                            declaringInterfaces[0].getName().equals(serviceName)) {
                        // TODO 待支持重载
                        if (funcMap.containsKey(method.getName())) {
                            throw new RuntimeException(serviceName + " repeat method " + method.getName());
                        }

                        Function<FRPCParam, FRPCReturn> callFunc = param -> {
                            try {
                                Object returnData = method.invoke(invokeObj, param.getData());
                                FRPCReturn frpcReturn = new FRPCReturn();
                                frpcReturn.setData(returnData);
                                return frpcReturn;
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e.getCause() == null ? e : e.getCause());
                            }
                        };

                        funcMap.put(method.getName(), callFunc);
                    }
                }

                mappings.put(serviceName, serviceMapping);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
