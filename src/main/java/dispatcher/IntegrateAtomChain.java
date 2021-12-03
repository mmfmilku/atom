package dispatcher;

import atom.Atom;
import param.Param;
import util.AssertUtils;

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

    @Override
    public Boolean execute(T param) {
        return invoke(param);
    }
}
