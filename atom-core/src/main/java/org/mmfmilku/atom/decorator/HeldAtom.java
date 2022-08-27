package org.mmfmilku.atom.decorator;


import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.param.Param;

public abstract class HeldAtom<T extends Param, H> implements Atom<T> {

    private H holder;

    HeldAtom(H holder) {
        this.holder = holder;
    }

    public H then() {
        return holder;
    }
}
