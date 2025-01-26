package org.mmfmilku.atom.transport.protocol.handle;

public interface ChannelContext<T> {

    default void write(T frame) {

    }

    default void cache(T frame) {

    }

    ServerHandle<?, T> getHandle();

}
