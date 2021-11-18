package param.operation;


import param.Param;

public interface Getter<T extends Param, K, V> {

    V get(T param, K k);

}
