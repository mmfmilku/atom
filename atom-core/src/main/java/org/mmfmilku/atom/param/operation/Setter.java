package org.mmfmilku.atom.param.operation;

import org.mmfmilku.atom.param.Param;

public interface Setter<T extends Param, K, V> {

    V set(T param, K k, V v);

}
