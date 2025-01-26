package org.mmfmilku.atom.transport.protocol.client;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.base.FFrame;
import org.mmfmilku.atom.transport.protocol.exception.ConnectException;

public class FClientSession implements ClientSession<FFrame> {

    private Connector connector;

    public FClientSession(Connector connector) {
        this.connector = connector;
    }

    public void send(byte[] data) {
        connector.write(MessageUtils.packFFrame(data));
    }

    @Override
    public void send(FFrame data) {
        connector.write(data);
    }

    public FFrame read() {
        Long readTimeout = 2000L;
        FFrame read = connector.read(readTimeout);
        if (read == null) {
            throw new ConnectException("read timeout");
        }
        if (0 == MessageUtils.decodeInt(read.getLen())) {
            connector.close();
            throw new ConnectException("connect closed");
        }
        return read;
    }

    @Override
    public FFrame sendThenRead(FFrame data) {
        connector.write(data);
        connector.flush();
        return read();
    }

    public byte[] sendThenRead(byte[] data) {
        return sendThenRead(MessageUtils.packFFrame(data)).getData();
    }

    public void close() {
        connector.close();
    }
}
