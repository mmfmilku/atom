package param.operation;

public interface Copier<T, K> {

    void copy(T param, K source, K target);

}
