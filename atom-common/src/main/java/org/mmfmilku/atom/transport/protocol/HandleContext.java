package org.mmfmilku.atom.transport.protocol;

import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;

public class HandleContext<T, R> {

    private ServerHandle<T, R> serverHandle;

    private Connector connector;

    public void write(R frame) {
        T decode = serverHandle.decode(frame);
        connector.write(decode);
    }

}
