package org.mmfmilku.atom.transport.frpc.server;

import java.io.Serializable;
import java.util.Arrays;

public class FRPCParam implements Serializable {

    private String serviceClass;

    private String apiName;

    private Object[] data;

    private FRPCReturn frpcReturn;

    public FRPCReturn getFrpcReturn() {
        return frpcReturn;
    }

    public void setFrpcReturn(FRPCReturn frpcReturn) {
        this.frpcReturn = frpcReturn;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FRPCParam{" +
                "serviceClass='" + serviceClass + '\'' +
                ", apiName='" + apiName + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
