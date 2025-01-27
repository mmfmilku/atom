package org.mmfmilku.atom.transport.protocol.client;

import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.base.FFrame;
import org.mmfmilku.atom.transport.protocol.exception.MessageCodeException;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;

import java.util.Arrays;

public class TypeClientSession extends ClientSessionDecorate<FFrame, TypeFrame>
        implements ClientSession<TypeFrame> {

    public TypeClientSession(ClientSession<FFrame> clientSession) {
        super(clientSession);
    }

    @Override
    protected TypeFrame code(FFrame fFrame) {
        byte[] fData = fFrame.getData();
        byte[] bytes = Arrays.copyOfRange(fData, 1, fData.length);
        TypeFrame typeFrame = new TypeFrame(fData[0], bytes);
        if (typeFrame.getType() == TypeFrame.ERROR) {
            throw new MessageCodeException(new String(typeFrame.getData()));
        }
        return typeFrame;
    }

    @Override
    protected FFrame decode(TypeFrame typeFrame) {
        if (typeFrame.getType() == TypeFrame.ERROR) {
            throw new MessageCodeException(new String(typeFrame.getData()));
        }
        FFrame fFrame = new FFrame();
        byte[] dataBytes = typeFrame.getData();
        int fDataLen = dataBytes.length + 1;
        byte[] fData = new byte[fDataLen];
        fData[0] = TypeFrame.DATA;
        System.arraycopy(dataBytes, 0, fData, 1, dataBytes.length);
        fFrame.setLen(MessageUtils.codeInt(fDataLen));
        fFrame.setData(fData);
        return fFrame;
    }

}
