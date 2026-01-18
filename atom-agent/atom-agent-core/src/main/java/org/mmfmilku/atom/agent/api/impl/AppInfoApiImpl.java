package org.mmfmilku.atom.agent.api.impl;

import org.mmfmilku.atom.agent.AtomAgentStarter;
import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.api.AgentPropertiesKey;
import org.mmfmilku.atom.agent.config.RunningConfig;
import org.mmfmilku.atom.agent.instrument.InstrumentationContext;
import org.mmfmilku.atom.agent.log.ScreenLogger;
import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.api.dto.BootConfigDTO;
import org.mmfmilku.atom.api.dto.FServerInfoDTO;
import org.mmfmilku.atom.api.dto.RunInfo;
import org.mmfmilku.atom.api.dto.RunningConfigDTO;
import org.mmfmilku.atom.transport.frpc.server.FRPCService;
import org.mmfmilku.atom.transport.frpc.server.FRPCStarter;
import org.mmfmilku.atom.transport.protocol.base.FServer;
import org.mmfmilku.atom.util.JavaUtil;

import java.util.List;
import java.util.Map;

@FRPCService
public class AppInfoApiImpl implements AppInfoApi {
    @Override
    public Integer ping() {
        return 1;
    }

    @Override
    public Map<Object, Object> info() {
        return AgentProperties.getProperties();
    }

    @Override
    public RunInfo runInfo() {
        String startClass = JavaUtil.getStartClass();
        RunInfo runInfo = new RunInfo();
        runInfo.setStartClass(startClass);

        // 设置启动时配置信息
        BootConfigDTO bootConfigDTO = new BootConfigDTO();
        bootConfigDTO.setCoreJarPath(AgentProperties.getProperty(AgentPropertiesKey.CORE_JAR_PATH));
        bootConfigDTO.setAppClassloader(AgentProperties.getProperty(AgentPropertiesKey.APP_CLASSLOADER));
        bootConfigDTO.setAppFserverDir(AgentProperties.getProperty(AgentPropertiesKey.FSERVER_DIR));
        runInfo.setBootConfigDTO(bootConfigDTO);

        // 设置运行时配置信息
        RunningConfigDTO runningConfigDTO = new RunningConfigDTO();
        RunningConfig runningConfig = AgentProperties.getRunningConfig();
        runningConfigDTO.setAppBasePackage(runningConfig.getAppBasePackage());
        runningConfigDTO.setByteCodeCompile(runningConfig.getByteCodeCompile());
        runningConfigDTO.setToStringMethod(runningConfig.getToStringMethod());
        runInfo.setRunningConfigDTO(runningConfigDTO);

        // 设置FServer运行时信息
        FServerInfoDTO fServerInfoDTO = new FServerInfoDTO();
        FRPCStarter frpcStarter = AtomAgentStarter.getInstance().frpcStarter;
        if (frpcStarter != null) {
            FServer fServer = frpcStarter.getfServer();
            FServer.FServerInfo fServerInfo = fServer.getFServerInfo();
            fServerInfoDTO.setListenPath(fServerInfo.listenPath);
            fServerInfoDTO.setConnectCount(fServerInfo.connectCount);
            fServerInfoDTO.setActiveCount(fServerInfo.activeCount);
            fServerInfoDTO.setPoolSize(fServerInfo.poolSize);
            fServerInfoDTO.setCorePoolSize(fServerInfo.corePoolSize);
            fServerInfoDTO.setMaximumPoolSize(fServerInfo.maximumPoolSize);
            fServerInfoDTO.setQueueSize(fServerInfo.queueSize);
            fServerInfoDTO.setQueueCapacity(fServerInfo.queueCapacity);
        }
        runInfo.setfServerInfoDTO(fServerInfoDTO);

        return runInfo;
    }

    @Override
    public RunningConfigDTO setRunningConfig(RunningConfigDTO runningConfigDTO) {
        RunningConfig runningConfig = AgentProperties.getRunningConfig();
        runningConfig.setAppBasePackage(runningConfigDTO.getAppBasePackage());
        runningConfig.setByteCodeCompile(runningConfigDTO.getByteCodeCompile());
        runningConfig.setToStringMethod(runningConfigDTO.getToStringMethod());
        AgentProperties.setRunningConfig(runningConfig);
        return runningConfigDTO;
    }

    @Override
    public Map<String, Object> getRunningOrd() {
        return InstrumentationContext.getOrdList();
    }

    @Override
    public Boolean stopAgent() {
        new Thread(() -> {
            try {
                // 延迟一秒，确保本次响应正常返回
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            AtomAgentStarter.getInstance().stopAgent();
        }).start();
        return true;
    }

    @Override
    public List<String> getAllScreenLogs() {
        return ScreenLogger.getAllLogs();
    }
}
