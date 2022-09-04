package org.mmfmilku.atom.dispatcher;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.exeption.AtomException;
import org.mmfmilku.atom.param.Param;
import org.mmfmilku.atom.util.AssertUtils;

public class IntegrateAtomChain<T extends Param> implements AtomChain<T>, AtomOperator<T> {

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

    @Override
    public void operate(String operate, Atom<T> atom) {
        if (!AtomOperatesConst.ADD.equals(operate)) {
            throw new AtomException(operate + " not support in DefaultAtomChain");
        }
        this.add(atom);
    }
}
