package org.mmfmilku.atom.agent;

import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.api.AgentPropertiesKey;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.transport.frpc.server.FRPCStarter;

import java.lang.instrument.Instrumentation;
import java.util.Objects;
import java.util.function.Function;

/**
 * 启动器
 */
public class AtomAgentStarter {

    private static AtomAgentStarter instance;

    public static AtomAgentStarter getInstance() {
        if (instance == null) {
            instance = new AtomAgentStarter();
        }
        return instance;
    }

    public FRPCStarter frpcStarter;

    private Thread shutdownHook;

    private Function<Object, Object> stopCallback;

    private AtomAgentStarter() {
        shutdownHook = new Thread(this::stopAgent);
    }

    public void setStopCallback(Function<Object, Object> stopCallback) {
        this.stopCallback = stopCallback;
    }

    public synchronized void start(String agentArgs, Instrumentation inst) {
        
        try {
            InstrumentationContext.init(inst);
            System.out.println("init InstrumentationContext end");

            // 初始化配置
            if (Objects.nonNull(agentArgs)) {
                AgentProperties.loadProperties(agentArgs);
                System.out.println("init properties end");
                System.out.println(AgentProperties.getInstance());
            }
            
            // 初始化自定义类路径
            String appClassLoader = AgentProperties.getProperty(AgentPropertiesKey.APP_CLASSLOADER);
            if (appClassLoader != null && appClassLoader.trim().length() > 0) {
                ClassLoader classLoader = InstrumentationContext.searchClassLoader(appClassLoader);
                if (classLoader != null) {
                    System.out.println("classPool add appClassLoader " + appClassLoader);
                    ByteCodeUtils.appendClassPath(classLoader);
                } else {
                    System.out.println("classPool add appClassLoader fail," + appClassLoader + " not exists");
                }
            }

            String fServerDir = AgentProperties.getProperty(AgentPropertiesKey.FSERVER_DIR);

            if (frpcStarter == null) {
                System.out.println("run FRPCStarter");
                frpcStarter = new FRPCStarter("org.mmfmilku.atom.agent.api.impl", fServerDir);
                frpcStarter.runServer();

                Runtime.getRuntime().addShutdownHook(shutdownHook);
            }

            System.out.println("agent end");
        } catch (Throwable e) {
            System.out.println("agent main error");
            e.printStackTrace();
            throw e;
        }
        
    }

    public void stopAgent() {
        // TODO 需要彻底释放加载的class
        if (frpcStarter != null) {
            frpcStarter.stopServer();
            frpcStarter = null;
        }
        if (stopCallback != null) {
            stopCallback.apply(null);
            stopCallback = null;
        }
        instance = null;
        InstrumentationContext.clear();
        // 移除关闭钩子
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        } catch (IllegalStateException e) {
            // 钩子已经被执行或不存在时会抛出异常，忽略
        }
        shutdownHook = null;
        AgentProperties.clear();
    }

}
