package org.mmfmilku.atom.web.console.domain;

import org.mmfmilku.atom.api.dto.ExecuteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JTerminalInfo {

    private String id;

    /**
     * 执行历史
     * */
    private List<String> history;

    /**
     * 结果历史
     * */
    private List<ExecuteResult> resultHistory = new ArrayList<>();

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

    public Set<String> getImportList() {
        return importList;
    }

    public void setImportList(Set<String> importList) {
        this.importList = importList;
    }

    public Map<String, Object> getContextVars() {
        return contextVars;
    }

    public void setContextVars(Map<String, Object> contextVars) {
        this.contextVars = contextVars;
    }

    public Map<String, String> getContextVarsType() {
        return contextVarsType;
    }

    public void setContextVarsType(Map<String, String> contextVarsType) {
        this.contextVarsType = contextVarsType;
    }

    public List<ExecuteResult> getResultHistory() {
        return resultHistory;
    }

    public void setResultHistory(List<ExecuteResult> resultHistory) {
        this.resultHistory = resultHistory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }
}
