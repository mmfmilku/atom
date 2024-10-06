package org.mmfmilku.atom.transport.handle;

import org.mmfmilku.atom.transport.ConnectContext;

public interface Handle<T> {

    void receive(T data, ConnectContext ctx);

}
