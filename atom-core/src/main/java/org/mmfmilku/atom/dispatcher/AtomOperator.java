package org.mmfmilku.atom.dispatcher;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.param.Param;

public interface AtomOperator<T extends Param> {

    void operate(String operate, Atom<T> atom);

}
