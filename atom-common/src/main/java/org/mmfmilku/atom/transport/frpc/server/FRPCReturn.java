package org.mmfmilku.atom.transport.frpc.server;

import java.io.Serializable;

public class FRPCReturn implements Serializable {

    private Boolean success;

    private FRPCException frpcException;

    private Object data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public FRPCException getFrpcException() {
        return frpcException;
    }

    public void setFrpcException(FRPCException frpcException) {
        this.frpcException = frpcException;
    }

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
