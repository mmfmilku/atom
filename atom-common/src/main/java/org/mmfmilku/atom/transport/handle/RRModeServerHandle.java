package org.mmfmilku.atom.transport.handle;

import org.mmfmilku.atom.transport.ConnectContext;

public class RRModeServerHandle implements ServerHandle<String> {

    @Override
    public boolean onOpen(ConnectContext ctx) {
        return true;
    }

    @Override
    public void onClose(ConnectContext ctx) {
    }

    @Override
    public void onError(ConnectContext ctx) {
    }

    @Override
    public void onReceive(ConnectContext ctx, String data) {

    }
}
