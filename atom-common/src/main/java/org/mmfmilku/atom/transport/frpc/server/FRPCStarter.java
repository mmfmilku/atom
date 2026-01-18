package org.mmfmilku.atom.transport.frpc.server;

import org.mmfmilku.atom.transport.protocol.base.FServer;
import org.mmfmilku.atom.transport.protocol.handle.assembly.TypeAssemblyHandler;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeHandler;
import org.mmfmilku.atom.util.AssertUtil;
import org.mmfmilku.atom.util.ReflectUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

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
            fServer = null;
        }
        scanPackage = null;
        fDir = null;
        classes = null;
        mappings = null;
        listener = null;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void run() {
        try {
            System.out.println("fServer listen " + fDir);
            fServer = new FServer(fDir)
                    .addHandle(new TypeHandler())
                    .addHandle(new TypeAssemblyHandler())
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

        List<String> scanClass = ReflectUtils.scanClass(scanPackage);
        for (String className : scanClass) {
            registerService(className);
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

    public FServer getfServer() {
        return fServer;
    }
}
