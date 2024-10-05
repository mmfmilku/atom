package org.mmfmilku.atom.agent.transport.handle;

import org.mmfmilku.atom.agent.transport.ConnectContext;

public class RRModeServerHandle implements ServerHandle<String> {

    private ServerHandle<String> serverHandle;

    public RRModeServerHandle(ServerHandle<String> serverHandle) {
        this.serverHandle = serverHandle;
    }

    @Override
    public boolean open(ConnectContext ctx) {
        return serverHandle.open(ctx);
    }

    @Override
    public void close(ConnectContext ctx) {
        serverHandle.close(ctx);
    }

    @Override
    public void receive(ConnectContext ctx, String data) {
        serverHandle.receive(ctx, data);
    }
}
