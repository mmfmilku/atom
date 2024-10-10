package org.mmfmilku.atom.transport.handle;

import org.mmfmilku.atom.transport.ConnectContext;

public interface ServerHandle<T> {

    boolean onOpen(ConnectContext ctx);

    void onClose(ConnectContext ctx);

    void onError(ConnectContext ctx);

    void onReceive(ConnectContext ctx, T data);

}
