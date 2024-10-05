package org.mmfmilku.atom.agent.transport.handle;

import org.mmfmilku.atom.agent.transport.ConnectContext;

public interface Handle<T> {

    void receive(T data, ConnectContext ctx);

}
