package org.mmfmilku.atom.exeption;

public class AtomException extends RuntimeException {

    public AtomException() {
        super();
    }

    public AtomException(String message) {
        super(message);
    }

    public AtomException(String message, Throwable cause) {
        super(message, cause);
    }

    public AtomException(Throwable cause) {
        super(cause);
    }
}
