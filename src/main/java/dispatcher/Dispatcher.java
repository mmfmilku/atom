package dispatcher;


import atom.Atom;
import param.Param;

public interface Dispatcher<T extends Param> extends Atom<T> {

    Dispatcher<T> add(Atom<T> atom);

    void invoke(T param);
}
