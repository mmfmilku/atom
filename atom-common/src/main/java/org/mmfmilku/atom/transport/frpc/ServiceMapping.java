package org.mmfmilku.atom.transport.frpc;

import java.util.Map;
import java.util.function.Function;

public class ServiceMapping {

    private Object invokeObj;

    Map<String, Function<byte[], byte[]>> funcMap;

    public Object getInvokeObj() {
        return invokeObj;
    }

    public void setInvokeObj(Object invokeObj) {
        this.invokeObj = invokeObj;
    }

    public Map<String, Function<byte[], byte[]>> getFuncMap() {
        return funcMap;
    }

    public void setFuncMap(Map<String, Function<byte[], byte[]>> funcMap) {
        this.funcMap = funcMap;
    }

    public Object execute(String apiName, Object data) {
        Function function = funcMap.get(apiName);
        if (function == null) {
            throw new RuntimeException("无此接口" + apiName);
        }
        return function.apply(data);
    }

}
