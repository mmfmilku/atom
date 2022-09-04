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

    public Object put(String k, String v) {
        return params.put(k, v);
    }

    public Object get(String k) {
        return params.get(k);
    }
}
