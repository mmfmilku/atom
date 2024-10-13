package org.mmfmilku.atom.web.console.service;

import org.mmfmilku.atom.agent.client.AgentClient;
import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.transport.frpc.client.FRPCFactory;
import org.mmfmilku.atom.util.AssertUtil;
import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Service
public class AgentService implements IAgentService {

    @Autowired
    IAgentConfigService agentConfigService;

    @Override
    public boolean loadAgent(String vmId, String appName, String basePackage) {
        AgentConfig config = agentConfigService.getConfig(appName);
        // TODO,ord文件读取不支持低柜文件夹，暂时使用ord目录
        String dir = config.getOrdDir();
        // TODO 配置 classloader
        String customClassloader = "org.springframework.boot.loader.LaunchedURLClassLoader";
        try {
            AgentClient.loadAgent(vmId, getAgentJar(), "base-path=" + dir
                    + ";app-classloader=" + customClassloader
                    + ";app-base-package=" + basePackage
                    + ";app-fserver-dir=" + config.getFDir()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        AppInfoApi infoApi = FRPCFactory.getService(AppInfoApi.class, config.getFDir());
        infoApi.ping();

        return true;
    }

    // TODO jar版本如何配置
    private static final String AGENT_JAR_NAME = "atom-agent-core-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
    private static final String AGENT_JAR_RESOURCE_PATH = "jar/" + AGENT_JAR_NAME;

    private String getAgentJar() {
        File agentFile = new File(AgentConfigService.CONSOLE_BASE_DIR, AGENT_JAR_RESOURCE_PATH);
        if (!agentFile.exists()) {
            InputStream jarInputStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(AGENT_JAR_RESOURCE_PATH);
            AssertUtil.notnull(jarInputStream, "can not find agentJar!");
            try {
                Files.copy(jarInputStream, agentFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return agentFile.getAbsolutePath();
    }
}
