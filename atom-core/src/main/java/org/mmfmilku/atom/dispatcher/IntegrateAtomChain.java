package org.mmfmilku.atom.dispatcher;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.param.Param;
import org.mmfmilku.atom.util.AssertUtils;

public class IntegrateAtomChain<T extends Param> implements AtomChain<T> {

    private Atom<T> integrationAtom;

    @Override
    public AtomChain<T> add(Atom<T> atom) {
        AssertUtils.notNull(atom);
        if (integrationAtom == null)
            integrationAtom = atom;
        else
            integrationAtom = integrationAtom.after(atom);
        return this;
    }

    @Override
    public Boolean invoke(T param) {
        return param != null && integrationAtom.execute(param);
    }

}
