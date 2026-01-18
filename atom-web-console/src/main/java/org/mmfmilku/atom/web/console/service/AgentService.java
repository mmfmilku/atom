package org.mmfmilku.atom.web.console.service;

import org.mmfmilku.atom.agent.client.AgentClient;
import org.mmfmilku.atom.api.AgentPropertiesKey;
import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.api.dto.RunInfo;
import org.mmfmilku.atom.transport.frpc.client.FRPCClient;
import org.mmfmilku.atom.transport.frpc.client.FRPCFactory;
import org.mmfmilku.atom.util.AssertUtil;
import org.mmfmilku.atom.util.StringUtils;
import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Map;

@Service
public class AgentService implements IAgentService {

    @Autowired
    IAgentConfigService agentConfigService;

    @Override
    public boolean loadAgent(String vmId, String appName) {
        AgentConfig config = agentConfigService.getConfigByName(appName);
//        String dir = config.getOrdDir();
        StringBuilder runningConfigStr = new StringBuilder();
        config.getConfigData().forEach((k,v) -> {
            runningConfigStr.append(";").append(k).append("=").append(v);
        });

        try {
            AgentClient.loadAgent(vmId, getAgentLoaderJar(),
                    // agent核心jar路径
                     AgentPropertiesKey.CORE_JAR_PATH + "=" + getAgentCoreJar()
                            // ferver监听路径
                            + ";" + AgentPropertiesKey.FSERVER_DIR + "=" + config.getFDir()
                            + runningConfigStr
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        AppInfoApi infoApi = FRPCFactory.getService(AppInfoApi.class, config.getFDir());
        infoApi.ping();

        RunInfo runInfo = infoApi.runInfo();
        System.out.println(runInfo);

        // TODO
        String basePath = config.getConfigData().get("basePath");
        if (StringUtils.isEmpty(basePath)) {
            config.getConfigData().put("basePath", runInfo.getStartClass());
        }

        return true;
    }

    @Override
    public boolean stopAgent(String appName) {
        AgentConfig config = agentConfigService.getConfigByName(appName);
        AppInfoApi infoApi = FRPCFactory.getService(AppInfoApi.class, config.getFDir());
        Boolean stopped = infoApi.stopAgent();
        // 释放客户端连接
        FRPCClient.getInstance(config.getFDir()).close();
        return stopped;
    }

    @Override
    public Map<String, String> vmInfo(String vmId) {
        Map<String, String> vmInfo = Collections.emptyMap();
        for (Map<String, String> vmMap : AgentClient.listVMMap()) {
            if (vmMap.get("vmId").equals(vmId)) {
                vmInfo = vmMap;
                break;
            }
        }

        // 判断是否连接
        AgentConfig config = agentConfigService.getConfigByName(vmInfo.get("displayName"));
        AppInfoApi infoApi = FRPCFactory.getService(AppInfoApi.class, config.getFDir());
        try {
            infoApi.ping();
            vmInfo.put("hasAgent", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vmInfo;
    }

    @Override
    public RunInfo agentInfo(String appName) {
        AgentConfig config = agentConfigService.getConfigByName(appName);
        return FRPCFactory.getService(AppInfoApi.class, config.getFDir()).runInfo();
    }

    // TODO jar版本如何配置
    private static final String CORE_JAR_NAME = "atom-agent-core-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
    private static final String LOADER_JAR_NAME = "atom-agent-loader-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
    private static final String CORE_JAR_RESOURCE_PATH = "jar/" + CORE_JAR_NAME;
    private static final String LOADER_JAR_RESOURCE_PATH = "jar/" + LOADER_JAR_NAME;

    private String getJarAbsolutePath(String resourcePath) {
        File baseDir = new File(AgentConfigService.CONSOLE_BASE_DIR, "jar");
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        File agentFile = new File(AgentConfigService.CONSOLE_BASE_DIR, resourcePath);
        if (!agentFile.exists()) {
            InputStream jarInputStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(resourcePath);
            AssertUtil.notnull(jarInputStream, "can not find agentJar!");
            try {
                agentFile.createNewFile();
                Files.copy(jarInputStream, agentFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return agentFile.getAbsolutePath();
    }

    private String getAgentLoaderJar() {
        return getJarAbsolutePath(LOADER_JAR_RESOURCE_PATH);
    }

    private String getAgentCoreJar() {
        return getJarAbsolutePath(CORE_JAR_RESOURCE_PATH);
    }
}
