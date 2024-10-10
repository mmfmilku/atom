package org.mmfmilku.atom.transport.frpc;

import java.util.Map;
import java.util.function.Function;

public class ServiceMapping {

    private Object invokeObj;

    Map<String, Function<FRPCParam, FRPCReturn>> funcMap;

    public Object getInvokeObj() {
        return invokeObj;
    }

    public void setInvokeObj(Object invokeObj) {
        this.invokeObj = invokeObj;
    }

    public void setFuncMap(Map<String, Function<FRPCParam, FRPCReturn>> funcMap) {
        this.funcMap = funcMap;
    }

    public FRPCReturn execute(String apiName, FRPCParam frpcParam) {
        Function<FRPCParam, FRPCReturn> function = funcMap.get(apiName);
        if (function == null) {
            throw new RuntimeException("无此接口" + apiName);
        }
        return function.apply(frpcParam);
    }

}
