package org.mmfmilku.atom.agent.transport.handle;

import org.mmfmilku.atom.agent.transport.MsgContext;

public interface Handle<T> {

    void receive(T data, MsgContext ctx);

}
