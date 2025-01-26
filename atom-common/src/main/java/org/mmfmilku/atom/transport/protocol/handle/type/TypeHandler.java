package org.mmfmilku.atom.transport.protocol.handle.type;

import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.exception.MessageCodeException;
import org.mmfmilku.atom.transport.protocol.base.FFrame;
import org.mmfmilku.atom.transport.protocol.handle.PipeLine;
import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;

import java.nio.charset.StandardCharsets;

/**
 * 设置消息类型
 * */
public class TypeHandler implements ServerHandle<FFrame, TypeFrame> {

    @Override
    public TypeFrame code(FFrame fFrame) {
        byte[] fData = fFrame.getData();
        if (fData.length == 0) {
            throw new MessageCodeException("error in TypeHandler, receive data is empty");
        }
        byte[] data = new byte[fData.length - 1];
        System.arraycopy(fData, 1, data, 0, data.length);
        return new TypeFrame(fData[0], data);
    }

    @Override
    public FFrame decode(TypeFrame typeFrame) {
        byte type = typeFrame.getType();
        byte[] data = typeFrame.getData();
        byte[] fData = new byte[1 + data.length];
        fData[0] = type;
        System.arraycopy(data, 0, fData, 1, data.length);
        FFrame fFrame = new FFrame();
        try {
            fFrame.setLen(MessageUtils.codeInt(fData.length));
        } catch (Exception e) {
            e.printStackTrace();
            data = e.getMessage().getBytes(StandardCharsets.UTF_8);
            fData = new byte[1 + data.length];
            fData[0] = TypeFrame.ERROR;
            System.arraycopy(data, 0, fData, 1, data.length);
            fFrame.setLen(MessageUtils.codeInt(fData.length));
        }
        fFrame.setData(fData);
        return fFrame;
    }

    @Override
    public void handle(FFrame fFrame, PipeLine pipeLine) {
        TypeFrame typeFrame = code(fFrame);
        if (typeFrame.getType() == TypeFrame.DATA) {
            pipeLine.handleNext(typeFrame);
        }
        // TODO
    }


}
