package org.mmfmilku.atom.dispatcher;


import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.param.Param;

public interface AtomChain<T extends Param> extends Atom<T> {

    AtomChain<T> add(Atom<T> atom);

    Boolean invoke(T param);

    /**
     *  AtomChain can be an Atom
     */
    @Override
    default Boolean execute(T param) {
        return invoke(param);
    }
}
