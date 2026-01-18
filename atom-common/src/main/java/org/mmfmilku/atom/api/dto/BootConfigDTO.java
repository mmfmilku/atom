package org.mmfmilku.atom.api.dto;

import java.io.Serializable;

/**
 * 启动时配置
 **/
public class BootConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String coreJarPath;
    private String appClassloader;
    private String appFserverDir;

    public String getCoreJarPath() {
        return coreJarPath;
    }

    public void setCoreJarPath(String coreJarPath) {
        this.coreJarPath = coreJarPath;
    }

    public String getAppClassloader() {
        return appClassloader;
    }

    public void setAppClassloader(String appClassloader) {
        this.appClassloader = appClassloader;
    }

    public String getAppFserverDir() {
        return appFserverDir;
    }

    public void setAppFserverDir(String appFserverDir) {
        this.appFserverDir = appFserverDir;
    }
}
