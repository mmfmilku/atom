package org.mmfmilku.atom.web.console.domain;

import java.util.List;

public class JTerminalInfo {

    private String id;

    private List<String> history;

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
