package org.mmfmilku.atom.api.dto;

import java.io.Serializable;

public class RunInfo implements Serializable {

    private String startClass;

    public String getStartClass() {
        return startClass;
    }

    public void setStartClass(String startClass) {
        this.startClass = startClass;
    }
}
