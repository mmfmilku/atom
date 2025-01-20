package org.mmfmilku.atom.transport.protocol.handle.type;

import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.exception.MessageCodeException;
import org.mmfmilku.atom.transport.protocol.base.FFrame;
import org.mmfmilku.atom.transport.protocol.handle.ChannelContext;
import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;

import java.util.Iterator;

/**
 * 设置消息类型
 * */
public class TypeHandler implements ServerHandle<FFrame, TypeFrame> {

    @Override
    public ChannelContext<TypeFrame> getChannelContext(ChannelContext<FFrame> channelContext) {
        return typeFrame -> {
            byte type = typeFrame.getType();
            byte[] data = typeFrame.getData();
            byte[] fData = new byte[1 + data.length];
            fData[0] = type;
            System.arraycopy(data, 0, fData, 1, data.length);
            FFrame fFrame = new FFrame();
            fFrame.setLen(MessageUtils.codeLength(fData.length));
            fFrame.setData(fData);
            channelContext.write(fFrame);
        };
    }

    @Override
    public void handle(FFrame fFrame, Iterator<ServerHandle> handleItr, ChannelContext<FFrame> channelContext) {
        byte[] fData = fFrame.getData();
        if (fData.length == 0) {
            throw new MessageCodeException("error in TypeHandler, receive data is empty");
        }
        byte[] data = new byte[fData.length - 1];
        System.arraycopy(fData, 1, data, 0, data.length);
        TypeFrame typeFrame = new TypeFrame(fData[0], data);

        if (typeFrame.getType() == TypeFrame.DATA) {
            // TODO
            handleNext(typeFrame, handleItr, channelContext);
        }
        // TODO
    }

}
