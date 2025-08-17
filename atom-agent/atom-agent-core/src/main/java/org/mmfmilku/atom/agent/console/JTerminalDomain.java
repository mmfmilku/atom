package org.mmfmilku.atom.agent.console;

import org.mmfmilku.atom.agent.compiler.parser.syntax.Import;

import java.util.*;

/**
 * 描述一个终端的对象
 * */
public class JTerminalDomain {

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
    private List<Import> importList = new ArrayList<>();

    /**
     * 变量上下文，每次执行定义的变量保存于此
     * k:变量名    v:变量值
     * */
    private Map<String, Object> contextVars = new HashMap<>();

    /**
     * 最近一次执行产生的变量
     * */
    private Set<String> currVars = new HashSet<>();

    /**
     * 执行历史
     * */
    private List<String> history = new ArrayList<>();

    public JTerminalDomain(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Set<String> getCurrVars() {
        return currVars;
    }

    public void setCurrVars(Set<String> currVars) {
        this.currVars = currVars;
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

    public List<Import> getImportList() {
        return importList;
    }

    public void setImportList(List<Import> importList) {
        this.importList = importList;
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
