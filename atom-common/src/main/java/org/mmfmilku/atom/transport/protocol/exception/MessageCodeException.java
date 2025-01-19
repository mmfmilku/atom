package org.mmfmilku.atom.transport.protocol.exception;

public class MessageCodeException extends RuntimeException {

    public MessageCodeException() {
    }

    public MessageCodeException(String message) {
        super(message);
    }

    public MessageCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageCodeException(Throwable cause) {
        super(cause);
    }

    public MessageCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
