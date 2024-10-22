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
import java.nio.file.StandardCopyOption;

@Service
public class AgentService implements IAgentService {

    @Autowired
    IAgentConfigService agentConfigService;

    @Override
    public boolean loadAgent(String vmId, String appName) {
        AgentConfig config = agentConfigService.getConfigByName(appName);
        String dir = config.getOrdDir();
        // TODO 配置 classloader
        String customClassloader = "org.springframework.boot.loader.LaunchedURLClassLoader";
        try {
            AgentClient.loadAgent(vmId, getAgentJar()
                    // 基础路径，ord文件、配置文件所在目录
                    , "base-path=" + dir
                            // 需要拓展的类加载器
                            + ";app-classloader=" + customClassloader
                            // 可重写class的包路径
                            // TODO 如何配置
                            + ";app-base-package=com.example.bootstudy"
                            // ferver监听路径
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
        File baseDir = new File(AgentConfigService.CONSOLE_BASE_DIR, "jar");
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        File agentFile = new File(AgentConfigService.CONSOLE_BASE_DIR, AGENT_JAR_RESOURCE_PATH);
        if (!agentFile.exists()) {
            InputStream jarInputStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(AGENT_JAR_RESOURCE_PATH);
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
}
