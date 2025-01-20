package org.mmfmilku.atom.transport.protocol.handle;

public interface ChannelContext<T> {

    void write(T frame);

}
