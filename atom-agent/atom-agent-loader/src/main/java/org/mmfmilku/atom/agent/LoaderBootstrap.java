package org.mmfmilku.atom.agent;

import org.mmfmilku.atom.agent.loader.AtomAgentClassLoader;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

/**
 * 入口启动类
 **/
public class LoaderBootstrap {

    static volatile ClassLoader atomClassLoader;

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("------------------------premain-----------------------");
        main(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("------------------------agentmain-----------------------");
        main(agentArgs, inst);
    }

    private synchronized static void main(String agentArgs, Instrumentation inst) {
//        runMonitor(inst);
        System.out.println("加载agent.....");
        System.out.println("加载agent getAllLoadedClasses().length:" + inst.getAllLoadedClasses().length);
        System.out.println("agentArgs=" + agentArgs);
        try {
            initAtomClassLoader(agentArgs, inst);
            // TODO 判断是否启动中，添加回调钩子释放 atomClassLoader = null
            startAtomAgent(agentArgs, inst);
            System.out.println("加载agent完成");
            System.out.println("加载agent完成 getAllLoadedClasses().length:" + inst.getAllLoadedClasses().length);
        } catch (Exception e) {
            System.out.println("加载agent失败");
            throw new RuntimeException(e);
        }
    }

    private static void initAtomClassLoader(String agentArgs, Instrumentation inst) throws MalformedURLException {
        if (atomClassLoader == null) {
            System.out.println("初始化atom类加载器...");
            String startClassName = LoaderUtil.getStartClass();
            Class<?> startClass = LoaderUtil.searchClass(inst, startClassName);
            ClassLoader appStartClassLoader = startClass.getClassLoader();

            String coreJarFilePath = "";
            for (String kv : agentArgs.split(";")) {
                if (kv.isEmpty()) {
                    continue;
                }
                String[] kvArr = kv.split("=");
                String k = kvArr[0];
                String v = kvArr[1];
                if ("coreJarPath".equals(k)) {
                    coreJarFilePath = v;
                    break;
                }
            }
            if (coreJarFilePath.length() == 0) {
                throw new RuntimeException("加载失败，缺少参数coreJarPath");
            }

            File coreJar = new File(coreJarFilePath);
            atomClassLoader = new AtomAgentClassLoader(new URL[]{coreJar.toURI().toURL()}, appStartClassLoader);
            System.out.println("初始化atom类加载器完成，父加载器：" + appStartClassLoader
                    + ",coreJarFilePath:" + coreJarFilePath);
        }
    }

    private static void startAtomAgent(String agentArgs, Instrumentation inst) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class<?> bootClass = atomClassLoader.loadClass("org.mmfmilku.atom.agent.AtomAgentStarter");
        Object instance = bootClass.getMethod("getInstance").invoke(null);
        bootClass.getMethod("setStopCallback", Function.class)
                        .invoke(instance, (Function) o -> {
                            atomClassLoader = null;
                            System.out.println("stop getAllLoadedClasses().length:" + inst.getAllLoadedClasses().length);
                            return null;
                        });
        bootClass.getMethod("start", String.class, Instrumentation.class)
                .invoke(instance, agentArgs, inst);
    }

    static volatile boolean monitorRunning = false;

    private static void runMonitor(Instrumentation inst) {
        if (monitorRunning) {
            return;
        }
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    if (atomClassLoader == null) {
                        System.out.println("atom类加载器为空");
                    } else {
                        System.out.println("atom类加载器运行中！！！");
                    }
                    Class<?> bootClass = LoaderUtil.searchClass(inst, "org.mmfmilku.atom.agent.AtomAgentStarter");
                    Object instance = bootClass.getMethod("getInstance").invoke(null);
                    System.out.println(instance);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        monitorRunning = true;
    }


}
