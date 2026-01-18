package org.mmfmilku.atom.api.dto;

import java.io.Serializable;

public class ExecuteResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;

    private Object executeReturn;

    private Throwable throwable;

    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

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
