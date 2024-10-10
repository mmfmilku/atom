package org.mmfmilku.atom.transport.handle.file;

import org.mmfmilku.atom.transport.ConnectContext;
import org.mmfmilku.atom.transport.handle.ServerHandle;

public class FHandle implements ServerHandle<String> {

    @Override
    public boolean onOpen(ConnectContext ctx) {
        ctx.write("PING");
        String read = ctx.read();
        return "PONG".equals(read);
    }

    @Override
    public void onClose(ConnectContext ctx) {
        // TODO 关闭标志
        ctx.write("");
        ctx.close();
    }

    @Override
    public void onError(ConnectContext ctx) {

    }

    @Override
    public void onReceive(ConnectContext ctx, String data) {
    }

}
