package org.mmfmilku.atom.transport.handle;

import org.mmfmilku.atom.transport.ConnectContext;

/**
 * SRClientHandle
 *
 * @author chenxp
 * @date 2024/9/30:13:40
 */
public class SRClientSession implements ClientSession<String> {
    
    private ConnectContext ctx;

    public SRClientSession(ConnectContext ctx) {
        this.ctx = ctx;
    }

    public void send(String data) {
        ctx.write(data);
    }

    public String read() {
        return ctx.read();
    }

    public String sendThenRead(String data) {
        send(data);
        ctx.flush();
        return read();
    }

    @Override
    public void close() {
        ctx.close();
    }
}
