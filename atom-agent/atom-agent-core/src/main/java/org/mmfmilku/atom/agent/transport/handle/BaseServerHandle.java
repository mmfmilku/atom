package org.mmfmilku.atom.agent.transport.handle;

import org.mmfmilku.atom.agent.transport.MsgContext;

public class BaseServerHandle implements ServerHandle<String> {

    @Override
    public boolean open(MsgContext ctx) {
        ctx.write("PING");
        String read = ctx.read();
        return "PONG".equals(read);
    }

    @Override
    public void close(MsgContext ctx) {
        // TODO 关闭标志
        ctx.write("");
        ctx.close();
    }

    @Override
    public void receive(MsgContext ctx, String data) {
        System.out.println("收到客户端数据:" + data);
        ctx.write("你好,收到你的数据:" + data);
    }

}
