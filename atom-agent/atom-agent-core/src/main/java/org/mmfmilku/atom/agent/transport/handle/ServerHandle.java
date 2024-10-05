package org.mmfmilku.atom.agent.transport.handle;

import org.mmfmilku.atom.agent.transport.ConnectContext;

public interface ServerHandle<T> {

    boolean open(ConnectContext ctx);

    void close(ConnectContext ctx);

    void receive(ConnectContext ctx, T data);

}
