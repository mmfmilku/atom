package atom;


import param.Param;

@FunctionalInterface
public interface Atom<T extends Param> {

    void execute(T param);

}
