package org.mmfmilku.atom.agent.transport.handle;

import org.mmfmilku.atom.agent.transport.MsgContext;

public interface ServerHandle<T> {
    
    void receive(MsgContext ctx, T data);

}
