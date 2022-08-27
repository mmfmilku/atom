package org.mmfmilku.atom.param.operation;


import org.mmfmilku.atom.param.Param;

public interface Getter<T extends Param, K, V> {

    V get(T param, K k);

}
