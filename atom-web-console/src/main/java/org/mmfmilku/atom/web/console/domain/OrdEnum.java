package org.mmfmilku.atom.web.console.domain;

import java.util.function.Function;

public enum OrdEnum {

    BASE_ORD("1", "重写文件", "java", AgentConfig::getOrdDir),
    STRATEGY_ORD("2", "重写策略", "jords", AgentConfig::getOrdDir),
    EXECUTE_ORD("3", "执行终端", "jconsole", AgentConfig::getExecuteDir),
    FOLDER_ORD("4", "文件夹", "", AgentConfig::getExecuteDir),
    SCRIPT_ORD("5", "脚本化文件", "jscript", AgentConfig::getExecuteDir),
    RESOURCE_ORD("6", "资源文件", "", AgentConfig::getExecuteDir),
    ;

    private OrdEnum(String type, String desc, String suffix, Function<AgentConfig, String> dirGetter) {
        this.type = type;
        this.desc = desc;
        this.suffix = suffix;
        this.dirGetter = dirGetter;
    }

    private String type;

    private String desc;

    private String suffix;

    private Function<AgentConfig, String> dirGetter;

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Function<AgentConfig, String> getDirGetter() {
        return dirGetter;
    }

    public void setDirGetter(Function<AgentConfig, String> dirGetter) {
        this.dirGetter = dirGetter;
    }
}
