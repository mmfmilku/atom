package org.mmfmilku.atom.transport.protocol.handle;

import org.mmfmilku.atom.transport.protocol.Connector;

import java.util.Iterator;

public interface ServerHandle<T, R> {

    R code(T t);

    T decode(R r);

    void handle(R r, Iterator<ServerHandle> handleItr);

    default void onOpen(Connector ctx) {}

    default void onOpenFail(Connector ctx) {}

    default void onClose(Connector ctx) {}

    default void onError(Connector ctx) {}

    default void doNext(Object frame, Iterator<ServerHandle> handleItr) {
        if (handleItr.hasNext()) {
            ServerHandle next = handleItr.next();
            Object code = next.code(frame);
            next.handle(code, handleItr);
        }
    }

}
