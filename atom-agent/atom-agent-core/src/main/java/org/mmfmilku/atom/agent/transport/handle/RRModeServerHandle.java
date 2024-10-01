package org.mmfmilku.atom.agent.transport.handle;

import org.mmfmilku.atom.agent.transport.MsgContext;

public class RRModeServerHandle implements ServerHandle<String> {

    private ServerHandle<String> serverHandle;

    public RRModeServerHandle(ServerHandle<String> serverHandle) {
        this.serverHandle = serverHandle;
    }

    @Override
    public boolean open(MsgContext ctx) {
        return serverHandle.open(ctx);
    }

    @Override
    public void close(MsgContext ctx) {
        serverHandle.close(ctx);
    }

    @Override
    public void receive(MsgContext ctx, String data) {
        serverHandle.receive(ctx, data);
    }
}
