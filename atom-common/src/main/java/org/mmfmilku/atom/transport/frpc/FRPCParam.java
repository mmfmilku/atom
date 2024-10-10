package org.mmfmilku.atom.transport.frpc;

import java.io.Serializable;
import java.util.Arrays;

public class FRPCParam implements Serializable {

    private String serviceClass;

    private String apiName;

    private Object[] data;

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
