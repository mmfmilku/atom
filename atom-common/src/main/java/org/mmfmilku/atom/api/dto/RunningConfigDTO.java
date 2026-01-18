package org.mmfmilku.atom.api.dto;

import java.io.Serializable;

/**
 * 运行时配置
 **/
public class RunningConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // toString方法：数据全局toString方法
    private String toStringMethod;

    // 可重写类基础包
    private String appBasePackage;

    // 使用字节码技术编译
    private Boolean byteCodeCompile;

    public String getToStringMethod() {
        return toStringMethod;
    }

    public void setToStringMethod(String toStringMethod) {
        this.toStringMethod = toStringMethod;
    }

    public String getAppBasePackage() {
        return appBasePackage;
    }

    public void setAppBasePackage(String appBasePackage) {
        this.appBasePackage = appBasePackage;
    }

    public Boolean getByteCodeCompile() {
        return byteCodeCompile;
    }

    public void setByteCodeCompile(Boolean byteCodeCompile) {
        this.byteCodeCompile = byteCodeCompile;
    }
}
