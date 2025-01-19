package org.mmfmilku.atom.transport.frpc.server;

public class FRPCException extends RuntimeException {

    public static final Integer ERR = 0;
    public static final Integer ERR_SYS = 1;
    public static final Integer ERR_BIZ = 2;

    private Integer errCode = ERR;

    public FRPCException(String message) {
        super(message);
    }

    public FRPCException(String message, int errCode) {
        super(message);
        this.errCode = errCode;
    }

    public Integer getErrCode() {
        return errCode;
    }
}
