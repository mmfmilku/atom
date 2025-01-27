package org.mmfmilku.atom.transport.protocol.handle;

import java.util.Collections;
import java.util.List;

public interface ServerHandle<IN, OUT> extends Codec<IN, OUT> {

    void handle(IN in, PipeLine pipeLine);

    OUT code(IN in);

    IN decode(OUT out);

    default List<IN> handleDecode(OUT out) {
        return Collections.singletonList(decode(out));
    }

    default void onOpen(PipeLine pipeLine) {}

    default void onOpenFail(PipeLine pipeLine) {}

    default void beforeClose(PipeLine pipeLine) {}

    default void afterClose(PipeLine pipeLine) {}

    default void onError(PipeLine pipeLine) {}

}
