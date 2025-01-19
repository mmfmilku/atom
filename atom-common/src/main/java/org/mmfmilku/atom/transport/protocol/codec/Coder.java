package org.mmfmilku.atom.transport.protocol.codec;

public interface Coder<I, O> {

    O code(I i);

    I decode(O o);

}
