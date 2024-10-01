package org.mmfmilku.atom.agent.transport.handle;

import org.mmfmilku.atom.agent.transport.MsgContext;

public interface ServerHandle<T> {

    boolean open(MsgContext ctx);

    void close(MsgContext ctx);

    void receive(MsgContext ctx, T data);

}
