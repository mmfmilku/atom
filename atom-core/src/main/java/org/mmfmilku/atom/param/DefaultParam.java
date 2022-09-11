package org.mmfmilku.atom.param;

import java.util.HashMap;
import java.util.Map;

public class DefaultParam extends BaseParam {

    private Map<String, Object> params;

    public DefaultParam() {
        this.params = new HashMap<>(4);
    }

    public DefaultParam(int initialCapacity) {
        this.params = new HashMap<>(initialCapacity);
    }

    public DefaultParam(Map<String, Object> params) {
        this.params = new HashMap<>(params.size());
        this.params.putAll(params);
    }

    public <T> T put(String k, Object v) {
        return (T) params.put(k, v);
    }

    public <T> T putIfAbsent(String k, String v) {
        return (T) params.putIfAbsent(k, v);
    }

    public <T> T get(String k) {
        return (T) params.get(k);
    }

    public <T> T get(String k, String defaultValue) {
        return (T) params.getOrDefault(k, defaultValue);
    }
}
