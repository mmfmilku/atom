package org.mmfmilku.atom.param;

public abstract class BaseParam implements Param {

    private Throwable lastCause;

    public Throwable getLastCause() {
        return lastCause;
    }

    public void setLastCause(Throwable lastCause) {
        this.lastCause = lastCause;
    }
}
