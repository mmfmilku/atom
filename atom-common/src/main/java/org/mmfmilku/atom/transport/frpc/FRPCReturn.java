package org.mmfmilku.atom.transport.frpc;

import java.io.Serializable;

public class FRPCReturn implements Serializable {

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FRPCReturn{" +
                "data=" + data +
                '}';
    }
}
