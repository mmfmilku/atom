package org.mmfmilku.atom.transport.protocol.client;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.base.FFrame;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;

import java.util.Arrays;

/**
 * SRClientHandle
 * Type协议
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
        if (read == null) {
            return "";
        }
        byte[] bytes = Arrays.copyOfRange(read.getData(), 1, read.getData().length);
        return new String(bytes);
    }

    public String sendThenRead(String data) {
        FFrame fFrame = new FFrame();
        byte[] dataBytes = data.getBytes();
        int fDataLen = dataBytes.length + 1;
        byte[] fData = new byte[dataBytes.length + 1];
        fData[0] = TypeFrame.DATA;
        System.arraycopy(dataBytes, 0, fData, 1, dataBytes.length);
        fFrame.setLen(MessageUtils.codeLength(fDataLen));
        fFrame.setData(fData);
        ctx.write(fFrame);
        ctx.flush();
        return read();
    }

    @Override
    public void close() {
        ctx.close();
    }
}
