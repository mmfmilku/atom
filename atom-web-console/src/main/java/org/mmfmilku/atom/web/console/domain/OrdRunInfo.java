package org.mmfmilku.atom.web.console.domain;

public class OrdRunInfo {

    private String ordName;

    private String running;

    private OrdEnum ordEnum;

    public OrdEnum getOrdEnum() {
        return ordEnum;
    }

    public void setOrdEnum(OrdEnum ordEnum) {
        this.ordEnum = ordEnum;
    }

    public String getOrdName() {
        return ordName;
    }

    public void setOrdName(String ordName) {
        this.ordName = ordName;
    }

    public String getRunning() {
        return running;
    }

    public void setRunning(String running) {
        this.running = running;
    }
}
