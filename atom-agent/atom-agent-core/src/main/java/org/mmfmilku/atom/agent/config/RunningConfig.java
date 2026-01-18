package org.mmfmilku.atom.agent.config;

/**
* 运行时配置
**/
public class RunningConfig {

    private String toStringMethod;

    private Boolean byteCodeCompile;

    private String appBasePackage;

    public String getToStringMethod() {
        return toStringMethod;
    }

    public void setToStringMethod(String toStringMethod) {
        this.toStringMethod = toStringMethod;
    }

    public Boolean getByteCodeCompile() {
        return byteCodeCompile;
    }

    public void setByteCodeCompile(Boolean byteCodeCompile) {
        this.byteCodeCompile = byteCodeCompile;
    }

    public String getAppBasePackage() {
        return appBasePackage;
    }

    public void setAppBasePackage(String appBasePackage) {
        this.appBasePackage = appBasePackage;
    }
}
