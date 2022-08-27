package org.mmfmilku.atom.param;

import org.mmfmilku.atom.param.operation.Getter;
import org.mmfmilku.atom.param.operation.Setter;

import java.util.HashMap;
import java.util.Map;

public class ParamContext<K, V> implements Param {

    private Map<K, V> param;
    private boolean custom;
    private Exception lastException;
    private Getter<ParamContext, K, V> getter;
    private Setter<ParamContext, K, V> setter;

    public ParamContext() {
        this.param = new HashMap<>();
    }

    public ParamContext(Map<K, V> param) {
        this.param = new HashMap<>(param);
    }

    public ParamContext(Getter<ParamContext, K, V> getter, Setter<ParamContext, K, V> setter) {
        this.getter = getter;
        this.setter = setter;
        this.custom = true;
    }

    public V getParam(K key) {
        if (custom) {
            return getter.get(this, key);
        }
        return param.get(key);
    }

    public V setParam(K key, V value) {
        if (custom) {
            return setter.set(this, key, value);
        }
        return param.put(key, value);
    }

    public Exception getLastException() {
        return lastException;
    }

    public void setLastException(Exception lastException) {
        this.lastException = lastException;
    }

    public Map<K, V> getParam() {
        return param;
    }
}
