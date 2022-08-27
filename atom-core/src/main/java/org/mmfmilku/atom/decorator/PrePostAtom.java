package org.mmfmilku.atom.decorator;


import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.param.Param;

public class PrePostAtom<T extends Param> implements Atom<T> {

    private Atom<T> pre;
    private Atom<T> post;
    private Atom<T> atom;

    public PrePostAtom(Atom<T> pre, Atom<T> post, Atom<T> atom) {
        this.pre = pre;
        this.post = post;
        this.atom = atom;
    }

    @Override
    public Boolean execute(T param) {
        pre.execute(param);
        Boolean success = atom.execute(param);
        post.execute(param);
        return success;
    }
}
