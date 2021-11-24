package atom;


import param.Param;

@FunctionalInterface
public interface Atom<T extends Param> {

    Boolean execute(T param);

}
