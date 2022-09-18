package org.mmfmilku.atom.test.param;

import org.mmfmilku.atom.param.DefaultParam;

public class TestParam extends DefaultParam {

    private String flags = "";

    public String appendFlag(String flag) {
        flags += flag;
        return flags;
    }

    public String getFlags() {
        return flags;
    }

}
