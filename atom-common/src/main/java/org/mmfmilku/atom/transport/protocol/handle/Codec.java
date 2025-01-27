package org.mmfmilku.atom.transport.protocol.handle;

public interface Codec<IN, OUT> {

    OUT code(IN in);

    IN decode(OUT out);

}
