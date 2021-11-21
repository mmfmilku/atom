package dispatcher;


import atom.Atom;
import param.Param;

public interface AtomChain<T extends Param> extends Atom<T> {

    AtomChain<T> add(Atom<T> atom);

    void invoke(T param);
}
