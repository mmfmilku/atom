package org.mmfmilku.atom.transport.protocol.handle;

import org.mmfmilku.atom.transport.protocol.Connector;

public interface Handle<T> {

    void receive(T data, Connector ctx);

}
