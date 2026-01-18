package org.mmfmilku.atom.web.console.service;

import org.mmfmilku.atom.api.AgentPropertiesKey;
import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.api.InstrumentApi;
import org.mmfmilku.atom.api.dto.RunningConfigDTO;
import org.mmfmilku.atom.transport.frpc.client.FRPCFactory;
import org.mmfmilku.atom.util.CodeUtils;
import org.mmfmilku.atom.util.StringUtils;
import org.mmfmilku.atom.web.console.domain.AgentConfig;
import org.mmfmilku.atom.web.console.domain.OrdEnum;
import org.mmfmilku.atom.web.console.domain.OrdFile;
import org.mmfmilku.atom.web.console.domain.OrdRunInfo;
import org.mmfmilku.atom.web.console.interfaces.IAgentConfigService;
import org.mmfmilku.atom.web.console.interfaces.IInstrumentService;
import org.mmfmilku.atom.web.console.interfaces.IOrdFileOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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

    @Value("${console.agentDefault.appBasePackage}")
    private String defaultAppBasePackage;

    @Value("${console.agentDefault.toStringMethod}")
    private String defaultToStringMethod;

    @Autowired
    IInstrumentService instrumentService;

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
                .append(v)
                .append("\n")
        );
        try (OutputStream os = Files.newOutputStream(Paths.get(configByName.getConfFile()))) {
            os.write(saveText.toString().getBytes(StandardCharsets.UTF_8));
            configMap.put(appName, configByName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("保存配置失败");
        }

        RunningConfigDTO runningConfigDTO = new RunningConfigDTO();
        runningConfigDTO.setAppBasePackage(saveData.get(AgentPropertiesKey.APP_BASE_PACKAGE));
        runningConfigDTO.setByteCodeCompile(Boolean.parseBoolean(
                saveData.getOrDefault(AgentPropertiesKey.BYTE_CODE_COMPILE, "false")));
        runningConfigDTO.setToStringMethod(saveData.get(AgentPropertiesKey.TO_STRING_METHOD));
        AppInfoApi appInfoApi = FRPCFactory.getService(AppInfoApi.class, configByName.getFDir());
        appInfoApi.setRunningConfig(runningConfigDTO);
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
        agentConfig.setExecuteDir(agentConfig.getAppBaseDir() + File.separator + "execute");
        agentConfig.setConfFile(agentConfig.getAppBaseDir() + File.separator + ".conf");

        // 设置应用基础包路径默认值
        agentConfig.getConfigData().put(AgentPropertiesKey.APP_BASE_PACKAGE, defaultAppBasePackage);
        // 设置应用toString方法默认值
        agentConfig.getConfigData().put(AgentPropertiesKey.TO_STRING_METHOD, defaultToStringMethod);

        try {
            Files.createDirectories(Paths.get(agentConfig.getAppBaseDir()));
            Files.createDirectories(Paths.get(agentConfig.getOrdDir()));
            Files.createDirectories(Paths.get(agentConfig.getTmpDir()));
            Files.createDirectories(Paths.get(agentConfig.getExecuteDir()));
            Paths.get(agentConfig.getConfFile()).toFile().createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("init agent config fail");
        }

        try (InputStream in = Files.newInputStream(
                Paths.get(agentConfig.getConfFile()), StandardOpenOption.CREATE)) {
            // TODO fix中文读取乱码
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
    public List<OrdRunInfo> listOrd(String appName, String childPath, OrdEnum ordEnum) {
        Map<String, Object> runningOrdClass =
                OrdEnum.BASE_ORD == ordEnum
                        ? instrumentService.getRunningOrdClass(appName)
                        : Collections.emptyMap();
        AgentConfig config = getConfigByName(appName);
        List<String> listFiles = StringUtils.isEmpty(childPath) ?
                ordFileOperation.listFiles(config, ordEnum)
                : ordFileOperation.listFiles(config, ordEnum, childPath);
        List<OrdRunInfo> ordRunInfoList = listFiles
                .stream()
                .map(ordFileName -> {
                    OrdRunInfo ordRunInfo = new OrdRunInfo();
                    ordRunInfo.setOrdName(ordFileName);
                    if (OrdEnum.BASE_ORD == ordEnum) {
                        ordRunInfo.setRunning(
                                runningOrdClass.containsKey(CodeUtils.toClassName(ordFileName)) ? "1" : "0");
                    }
                    ordRunInfo.setOrdEnum(OrdEnum.gussEnum(ordFileName));
                    return ordRunInfo;
                }).collect(Collectors.toList());
        return ordRunInfoList;
    }

    @Override
    public void deleteOrd(String appName, String childPath, OrdEnum ordEnum) {
        AgentConfig config = getConfigByName(appName);
        OrdFile ordFile = new OrdFile();
        ordFile.setFileName(childPath);
        ordFile.setOrdId(config.getId());
        ordFileOperation.delete(config, ordFile, ordEnum);
    }

    @Override
    public OrdFile readOrd(String appName, String ordFileName, OrdEnum ordEnum) {
        AgentConfig config = getConfigByName(appName);
        OrdFile ord = ordFileOperation.getOrd(config, ordFileName, ordEnum);
        Map<String, Object> runningOrdClass = instrumentService.getRunningOrdClass(appName);
        ord.setRunning(runningOrdClass.containsKey(CodeUtils.toClassName(ordFileName)) ? "1" : "0");
        return ord;
    }

    @Override
    public void writeOrd(String appName, OrdFile ordFile, OrdEnum ordEnum) {
        AgentConfig config = getConfigByName(appName);
        ordFile.setOrdId(config.getId());
        ordFile.setFileName(ordFileNameFormat(ordFile, ordEnum));
        ordFileOperation.setText(config, ordFile, ordEnum);
    }
    
    private String ordFileNameFormat(OrdFile ordFile, OrdEnum ordEnum) {
        String ordFileName = ordFile.getFileName();
        if (StringUtils.isEmpty(ordEnum.getSuffix())) {
            return ordFileName;
        }
        String suffix = ordEnum.getSuffix();
        // .ord 或 .java
        if (ordFileName.endsWith(suffix)) {
            return ordFileName;
        }
        if (ordFileName.endsWith(".")) {
            return ordFileName + suffix.substring(1);
        }
        return ordFileName + suffix;
    }

}
