package org.mmfmilku.atom.web.console.service;

import org.mmfmilku.atom.consts.CodeConst;
import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.domain.OrdFile;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IOrdFileOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * AgentConfigService
 *
 * @author mmfmilku
 * @date 2024/7/30:19:46
 */
@Service
public class AgentConfigService implements IAgentConfigService {
    
    public static final String CONSOLE_BASE_DIR = System.getProperty("user.dir") + File.separator + "AgentData";

    private static final ConcurrentMap<String, AgentConfig> configMap = new ConcurrentHashMap<>();
    
    public static final String ORD_SUFFIX = ".ord";

    private static MessageDigest sha1;

    static {
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    @Autowired
    private IOrdFileOperation ordFileOperation;

    @Override
    public Collection<AgentConfig> list() {
        return configMap.values();
    }

    @Override
    public void saveConfig(String appName, Map<String, String> saveData) {
        // 仅覆盖map
        AgentConfig configByName = getConfigByName(appName);
        configByName.getConfigData().clear();
        configByName.getConfigData().putAll(saveData);
        StringBuilder saveText = new StringBuilder();
        saveData.forEach((k, v) ->
                saveText.append(k)
                .append("=")
                .append(v));
        try (OutputStream os = Files.newOutputStream(Paths.get(configByName.getConfFile()))) {
            os.write(saveText.toString().getBytes(StandardCharsets.UTF_8));
            configMap.put(appName, configByName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("保存配置失败");
        }
    }

    @Override
    public AgentConfig getConfigByName(String appName) {
        if (configMap.containsKey(appName)) {
            return configMap.get(appName);
        }
        synchronized (configMap) {
            // double check
            if (!configMap.containsKey(appName)) {
                configMap.put(appName, initAgentConfig(appName));
            }
        }
        return configMap.get(appName);
    }

    private AgentConfig initAgentConfig(String appName) {
        String id = getId(appName);
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setId(id);
        agentConfig.setAppBaseDir(CONSOLE_BASE_DIR + File.separator + id);
        agentConfig.setFDir(agentConfig.getAppBaseDir() + File.separator + "fserver");
        agentConfig.setOrdDir(agentConfig.getAppBaseDir() + File.separator + "ord");
        agentConfig.setTmpDir(agentConfig.getAppBaseDir() + File.separator + "tmp");
        agentConfig.setConfFile(agentConfig.getAppBaseDir() + File.separator + ".conf");

        try {
            Files.createDirectories(Paths.get(agentConfig.getAppBaseDir()));
            Files.createDirectories(Paths.get(agentConfig.getOrdDir()));
            Files.createDirectories(Paths.get(agentConfig.getTmpDir()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("init agent config fail");
        }

        try (InputStream in = Files.newInputStream(
                Files.createFile(Paths.get(agentConfig.getConfFile())))) {
            Properties properties = new Properties();
            properties.load(in);
            properties.forEach((k, v) -> agentConfig.getConfigData().put((String) k, (String) v));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("init agent config fail");
        }
        return agentConfig;
    }

    private String getId(String appName) {
        sha1.update(appName.getBytes(StandardCharsets.UTF_8));
        byte[] digest = sha1.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    public List<String> listOrd(String appName) {
        AgentConfig config = getConfigByName(appName);
        return ordFileOperation.listFiles(config);
    }

    @Override
    public void deleteOrd(String appName, String ordFileName) {
        AgentConfig config = getConfigByName(appName);
        OrdFile ordFile = new OrdFile();
        ordFile.setFileName(ordFileName);
        ordFile.setOrdId(config.getId());
        ordFileOperation.delete(config, ordFile);
    }

    @Override
    public OrdFile readOrd(String appName, String ordFileName) {
        AgentConfig config = getConfigByName(appName);
        return ordFileOperation.getOrd(config, ordFileName);
    }

    @Override
    public void writeOrd(String appName, OrdFile ordFile) {
        AgentConfig config = getConfigByName(appName);
        ordFile.setOrdId(config.getId());
        ordFile.setFileName(ordFileNameFormat(ordFile.getFileName()));
        ordFileOperation.setText(config, ordFile);
    }
    
    private String ordFileNameFormat(String ordFileName) {
        // .ord 或 .java
        if (ordFileName.endsWith(ORD_SUFFIX) || ordFileName.endsWith(CodeConst.JAVA_FILE_SUFFIX)) {
            return ordFileName;
        }
        if (ordFileName.endsWith(".")) {
            return ordFileName + ORD_SUFFIX.substring(1);
        }
        return ordFileName + ORD_SUFFIX;
    }

}
