package org.mmfmilku.atom.decorator;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.param.BaseParam;
import org.mmfmilku.atom.util.AssertUtils;

import java.util.function.BiFunction;

public class TryAtom<T extends BaseParam> implements Atom<T> {

    private Atom<T> atom;
    private BiFunction<Exception, T, Boolean> catchProcess;
    private Atom<T> finallyProcess;

    public TryAtom(Atom<T> atom) {
        this.atom = atom;
    }

    public TryAtom<T> catchProcess(Atom<T> catchProcess) {
        AssertUtils.notNull(catchProcess);
        this.catchProcess = (exception, param) -> catchProcess.execute(param);
        return this;
    }

    public TryAtom<T> catchProcess(BiFunction<Exception, T, Boolean> catchProcess) {
        AssertUtils.notNull(catchProcess);
        this.catchProcess = catchProcess;
        return this;
    }

    public TryAtom<T> finallyProcess(Atom<T> finallyProcess) {
        AssertUtils.notNull(finallyProcess);
        this.finallyProcess = finallyProcess;
        return this;
    }

    @Override
    public Boolean execute(T param) {
        try {
            return atom.execute(param);
        } catch (Exception e) {
            param.setLastCause(e);
            if (catchProcess != null)
                return catchProcess.apply(e, param);
            return true;
        } finally {
            if (finallyProcess != null)
                finallyProcess.execute(param);
        }
    }
}
