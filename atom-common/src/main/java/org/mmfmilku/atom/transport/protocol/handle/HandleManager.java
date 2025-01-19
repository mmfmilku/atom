package org.mmfmilku.atom.transport.protocol.handle;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.frame.FFrame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HandleManager {

    private List<ServerHandle> handles = new ArrayList<>();

    private long readTimeOutMillis = 0;

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
            handles.forEach(h -> h.onOpen(ctx));
        } else {
            handles.forEach(h -> h.onOpenFail(ctx));
        }
        return open;
    }

    public void onClose(Connector ctx) {
        // 在真正关闭之前触发
        handles.forEach(h -> h.onClose(ctx));
        // TODO 关闭标志
        ctx.write("");
        ctx.close();
    }

    public void onError(Connector ctx) {
        handles.forEach(h -> h.onError(ctx));
    }

    public void onReceive(Connector ctx, FFrame fFrame) {
        Iterator<ServerHandle> handleItr = handles.iterator();
        if (handleItr.hasNext()) {
            ServerHandle next = handleItr.next();
            Object code = next.code(fFrame);
            next.handle(code, handleItr);
        }
    }

    public void addHandle(ServerHandle handle) {
        handles.add(handle);
    }

}
