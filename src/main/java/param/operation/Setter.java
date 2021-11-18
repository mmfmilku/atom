package param.operation;


import param.Param;

public interface Setter<T extends Param, K, V> {

    void set(T param, K k, V v);

}
