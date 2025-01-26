package org.mmfmilku.atom.transport.protocol.handle;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.base.FFrame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HandleManager {

    private final List<ServerHandle> handles = new ArrayList<>();

    private long readTimeOutMillis = 2000;

    public void setReadTimeOutMillis(long readTimeOutMillis) {
        this.readTimeOutMillis = readTimeOutMillis;
    }

    public boolean openConnect(Connector ctx) {
        FFrame[] pingPong = MessageUtils.getPingPong();
        FFrame pingFrame = pingPong[0];
        FFrame pongFrame = pingPong[1];
        ctx.write(pingFrame);
        FFrame read = ctx.read(readTimeOutMillis);
        boolean open = MessageUtils.equals(pongFrame, read);
        if (open) {
            ctx.write(MessageUtils.packFFrame((byte) 1));
            handles.forEach(h -> h.onOpen(getChannelContext(ctx)));
        } else {
            ctx.write(MessageUtils.packFFrame((byte) 0));
            handles.forEach(h -> h.onOpenFail(getChannelContext(ctx)));
        }
        return open;
    }

    private ChannelContext<FFrame> getChannelContext(Connector connector) {
        return connector::write;
    }

    public void closeConnect(Connector connector) {
        // 在真正关闭之前触发
        handles.forEach(h -> h.beforeClose(getChannelContext(connector)));
        connector.close();
        handles.forEach(h -> h.afterClose(getChannelContext(connector)));
    }

    public void onError(Connector ctx) {
        handles.forEach(h -> h.onError(getChannelContext(ctx)));
    }

    public void onReceive(Connector connector, FFrame fFrame) {
        if (MessageUtils.decodeInt(fFrame.getLen()) == 0) {
            // 关闭连接消息
            closeConnect(connector);
            return;
        }
        Iterator<ServerHandle> handleItr = handles.iterator();
        if (handleItr.hasNext()) {
            ServerHandle next = handleItr.next();
            next.handle(fFrame, handleItr, getChannelContext(connector));
        }
    }

    public void addHandle(ServerHandle handle) {
        handles.add(handle);
    }

}
