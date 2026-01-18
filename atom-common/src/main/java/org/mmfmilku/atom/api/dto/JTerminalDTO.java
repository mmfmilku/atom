package org.mmfmilku.atom.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 描述一个终端的对象
 * */
public class JTerminalDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 终端id，唯一
     * */
    private String id;

    /**
     * 终端名称
     * */
    private String name;

    /**
     * 全局import
     * */
    private Set<String> importList = new HashSet<>();

    /**
     * 变量上下文，每次执行定义的变量保存于此
     * k:变量名    v:变量值
     * */
    private Map<String, Object> contextVars = new HashMap<>();

    /**
     * 变量类型上下文，每次执行定义的变量保存于此
     * k:变量名    v:变量类型
     * */
    private Map<String, String> contextVarsType = new HashMap<>();

    /**
     * 执行历史
     * */
    private List<String> history = new ArrayList<>();

    /**
     * 结果历史
     * */
    private List<ExecuteResult> resultHistory = new ArrayList<>();

    public JTerminalDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Set<String> getImportList() {
        return importList;
    }

    public void setImportList(Set<String> importList) {
        this.importList = importList;
    }

    public List<ExecuteResult> getResultHistory() {
        return resultHistory;
    }

    public void setResultHistory(List<ExecuteResult> resultHistory) {
        this.resultHistory = resultHistory;
    }

    public Map<String, String> getContextVarsType() {
        return contextVarsType;
    }

    public void setContextVarsType(Map<String, String> contextVarsType) {
        this.contextVarsType = contextVarsType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getContextVars() {
        return contextVars;
    }

    public void setContextVars(Map<String, Object> contextVars) {
        this.contextVars = contextVars;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }
}
