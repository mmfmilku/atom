package org.mmfmilku.atom.agent.transport.handle;

import org.mmfmilku.atom.agent.transport.ConnectContext;

public class BaseServerHandle implements ServerHandle<String> {

    @Override
    public boolean open(ConnectContext ctx) {
        ctx.write("PING");
        String read = ctx.read();
        return "PONG".equals(read);
    }

    @Override
    public void close(ConnectContext ctx) {
        // TODO 关闭标志
        ctx.write("");
        ctx.close();
    }

    @Override
    public void receive(ConnectContext ctx, String data) {
        System.out.println("收到客户端数据:" + data);
        ctx.write("你好,收到你的数据:" + data);
    }

}
