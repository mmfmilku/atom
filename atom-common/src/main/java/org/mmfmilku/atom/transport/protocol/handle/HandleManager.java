package org.mmfmilku.atom.transport.protocol.handle;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.base.FFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class HandleManager {

    private final List<ServerHandle> handles = new ArrayList<>();

    private long readTimeOutMillis = 2000;

    public void setReadTimeOutMillis(long readTimeOutMillis) {
        this.readTimeOutMillis = readTimeOutMillis;
    }

    public boolean openConnect(Connector connector) {
        FFrame[] pingPong = MessageUtils.getPingPong();
        FFrame pingFrame = pingPong[0];
        FFrame pongFrame = pingPong[1];
        connector.write(pingFrame);
        FFrame read = connector.read(readTimeOutMillis);
        boolean open = MessageUtils.equals(pongFrame, read);
        if (open) {
            connector.write(MessageUtils.packFFrame((byte) 1));
            eventTrig(ServerHandle::onOpen, newPipeLine(connector));
        } else {
            connector.write(MessageUtils.packFFrame((byte) 0));
            eventTrig(ServerHandle::onOpenFail, newPipeLine(connector));
        }
        return open;
    }

    private PipeLine newPipeLine(Connector connector) {
        PipeLine pipeLine = new PipeLine(connector);
        pipeLine.getHandleList().addAll(handles);
        return pipeLine;
    }

    private void eventTrig(BiConsumer<ServerHandle, PipeLine> biConsumer, PipeLine pipeLine) {
        if (handles.size() > 0) biConsumer.accept(handles.get(0), pipeLine);
    }

    public void closeConnect(Connector connector) {
        // 在真正关闭之前触发
        eventTrig(ServerHandle::beforeClose, newPipeLine(connector));
        connector.close();
        eventTrig(ServerHandle::afterClose, newPipeLine(connector));
    }

    public void onError(Connector connector) {
        eventTrig(ServerHandle::onError, newPipeLine(connector));
    }

    public void onReceive(Connector connector, FFrame fFrame) {
        if (MessageUtils.decodeInt(fFrame.getLen()) == 0) {
            // 关闭连接消息
            closeConnect(connector);
            return;
        }
        PipeLine pipeLine = new PipeLine(connector);
        pipeLine.getHandleList().addAll(handles);

        pipeLine.handleNext(fFrame);
    }

    public void addHandle(ServerHandle handle) {
        handles.add(handle);
    }

}
