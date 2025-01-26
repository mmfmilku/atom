package org.mmfmilku.atom.transport.protocol.handle;

public interface ServerHandle<IN, OUT> {

    void handle(IN in, PipeLine pipeLine);

    OUT code(IN in);

    IN decode(OUT out);

    default void onOpen(PipeLine pipeLine) {}

    default void onOpenFail(PipeLine pipeLine) {}

    default void beforeClose(PipeLine pipeLine) {}

    default void afterClose(PipeLine pipeLine) {}

    default void onError(PipeLine pipeLine) {}

}
