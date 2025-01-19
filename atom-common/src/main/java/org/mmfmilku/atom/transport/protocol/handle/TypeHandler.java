package org.mmfmilku.atom.transport.protocol.handle;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.exception.MessageCodeException;
import org.mmfmilku.atom.transport.protocol.frame.FFrame;
import org.mmfmilku.atom.transport.protocol.frame.TypeFrame;

import java.util.Iterator;

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
        // TODO
        return null;
    }

    @Override
    public void handle(TypeFrame typeFrame, Iterator<ServerHandle> handleItr) {
        if (typeFrame.getType() == TypeFrame.DATA) {
            // TODO
            doNext(typeFrame, handleItr);
        }
        // TODO
    }

}
