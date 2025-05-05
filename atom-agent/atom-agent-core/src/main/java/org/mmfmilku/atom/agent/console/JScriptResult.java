package org.mmfmilku.atom.agent.console;

public class JScriptResult {

    private boolean success;

    private Object executeReturn;

    private Throwable throwable;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getExecuteReturn() {
        return executeReturn;
    }

    public void setExecuteReturn(Object executeReturn) {
        this.executeReturn = executeReturn;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

}
