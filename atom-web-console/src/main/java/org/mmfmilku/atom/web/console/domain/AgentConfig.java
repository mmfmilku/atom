package org.mmfmilku.atom.web.console.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * AgentConfig
 *
 * @author mmfmilku
 * @date 2024/7/31:9:13
 */
public class AgentConfig {
    
    /**
     * 根据应用启动命令映射的id
     * */
    private String id;

    /**
     * 为每个应用分配的基础路径
     * */
    private String appBaseDir;

    /**
     * 应用ORD存放路径
     * */
    private String ordDir;

    /**
     * 应用FServer监听路径
     * */
    private String fDir;

    /**
     * 应用缓存文件
     * */
    private String tmpDir;

    /**
     * 应用配置文件
     * */
    private String confFile;

    /**
     * 可修改配置
     * */
    private Map<String, String> configData = new HashMap<>();

    public Map<String, String> getConfigData() {
        return configData;
    }

    public String getConfFile() {
        return confFile;
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

    public String getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    public String getAppBaseDir() {
        return appBaseDir;
    }

    public void setAppBaseDir(String appBaseDir) {
        this.appBaseDir = appBaseDir;
    }

    public String getOrdDir() {
        return ordDir;
    }

    public void setOrdDir(String ordDir) {
        this.ordDir = ordDir;
    }

    public String getFDir() {
        return fDir;
    }

    public void setFDir(String fDir) {
        this.fDir = fDir;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
