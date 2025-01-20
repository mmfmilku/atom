package org.mmfmilku.atom.transport.protocol.client;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.base.FFrame;

/**
 * SRClientHandle
 *
 * @author chenxp
 * @date 2024/9/30:13:40
 */
public class SRClientSession implements ClientSession<String> {
    
    private Connector ctx;

    public SRClientSession(Connector ctx) {
        this.ctx = ctx;
    }

    public void send(String data) {
        ctx.write(data);
    }

    public String read() {
        FFrame read = ctx.read(2000);
        return new String(read.getData());
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
