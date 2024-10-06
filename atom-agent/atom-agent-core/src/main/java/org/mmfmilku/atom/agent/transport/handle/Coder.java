package org.mmfmilku.atom.agent.transport.handle;

public interface Coder<I, O> {

    O code(I i);

    I decode(O o);

}
