package org.mmfmilku.atom.api.dto;

import java.io.Serializable;

public class RunInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 启动类
    private String startClass;

    // 启动时配置（不可动态修改）
    private BootConfigDTO bootConfigDTO;

    // 运行时配置（可动态修改）
    private RunningConfigDTO runningConfigDTO;

    // FServer运行时信息
    private FServerInfoDTO fServerInfoDTO;

    public String getStartClass() {
        return startClass;
    }

    public void setStartClass(String startClass) {
        this.startClass = startClass;
    }

    public BootConfigDTO getBootConfigDTO() {
        return bootConfigDTO;
    }

    public void setBootConfigDTO(BootConfigDTO bootConfigDTO) {
        this.bootConfigDTO = bootConfigDTO;
    }

    public RunningConfigDTO getRunningConfigDTO() {
        return runningConfigDTO;
    }

    public void setRunningConfigDTO(RunningConfigDTO runningConfigDTO) {
        this.runningConfigDTO = runningConfigDTO;
    }

    public FServerInfoDTO getfServerInfoDTO() {
        return fServerInfoDTO;
    }

    public void setfServerInfoDTO(FServerInfoDTO fServerInfoDTO) {
        this.fServerInfoDTO = fServerInfoDTO;
    }
}
