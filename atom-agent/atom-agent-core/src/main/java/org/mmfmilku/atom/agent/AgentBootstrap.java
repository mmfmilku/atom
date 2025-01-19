package org.mmfmilku.atom.agent;

import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.handle.AgentHandle;
import org.mmfmilku.atom.agent.instrument.transformer.FileDefineTransformer;
import org.mmfmilku.atom.agent.instrument.transformer.ParamPrintTransformer;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.transport.frpc.server.FRPCStarter;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AgentHelper
 *
 * @author mmfmilku
 * @date 2024/6/4:15:02
 */
public class AgentBootstrap {

    private static List<AgentHandle> handleChain = new ArrayList<>();

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("------------------------premain-----------------------");
//        handleChain.add(new PreMainHandle());
        main(agentArgs, inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("------------------------agentmain-----------------------");
//        handleChain.add(new AgentMainHandle());
        main(agentArgs, inst);
    }

    private static FRPCStarter frpcStarter;

    private static Thread shutdownHook = new Thread(AgentBootstrap::stopAgent);

    public synchronized static void main(String agentArgs, Instrumentation inst) {
        
        try {
            InstrumentationContext.init(inst);

            // 初始化配置
            if (Objects.nonNull(agentArgs)) {
                AgentProperties.loadProperties(agentArgs);
                System.out.println("init properties end");
                System.out.println(AgentProperties.getInstance());
//                OverrideBodyHolder.load(AgentProperties.getProperty(AgentProperties.PROP_BASE_PATH));
            }
            
            // 初始化自定义类路径
            String appClassLoader = AgentProperties.getProperty(AgentProperties.PROP_APP_CLASSLOADER);
            if (appClassLoader != null && appClassLoader.trim().length() > 0) {
                ClassLoader classLoader = InstrumentationContext.searchClassLoader(appClassLoader);
                if (classLoader != null) {
                    System.out.println("classPool add appClassLoader " + appClassLoader);
                    ByteCodeUtils.appendClassPath(classLoader);
                } else {
                    System.out.println("classPool add appClassLoader fail");
                }
            }

            for (AgentHandle handle : handleChain) {
                handle.handle(agentArgs, inst);
            }

            InstrumentationContext.addTransformer(new FileDefineTransformer());
            InstrumentationContext.addTransformer(new ParamPrintTransformer());

            String fServerDir = AgentProperties.getProperty(AgentProperties.PROP_FSERVER_DIR);

            if (AgentBootstrap.frpcStarter == null) {
                FRPCStarter frpcStarter = new FRPCStarter("org.mmfmilku.atom.agent.api.impl", fServerDir);
                AgentBootstrap.frpcStarter = frpcStarter;
                frpcStarter.runServer();

                Runtime.getRuntime().addShutdownHook(shutdownHook);
            }
        } catch (Throwable e) {
            System.out.println("agent main error");
            e.printStackTrace();
            throw e;
        }
        
    }

    public static void stopAgent() {
        if (frpcStarter != null) {
            frpcStarter.stopServer();
        }
    }

}
