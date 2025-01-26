package org.mmfmilku.atom.transport.protocol.handle.string;

import org.mmfmilku.atom.transport.protocol.handle.PipeLine;
import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringHandle implements ServerHandle<TypeFrame, String> {

    private Charset charset = StandardCharsets.UTF_8;

    @Override
    public void handle(TypeFrame typeFrame, PipeLine pipeLine) {
        byte type = typeFrame.getType();
        if (TypeFrame.DATA == type) {
            pipeLine.handleNext(code(typeFrame));
        } else {
            // TODO
        }
    }

    @Override
    public String code(TypeFrame typeFrame) {
        byte[] data = typeFrame.getData();
        return new String(data);
    }

    @Override
    public TypeFrame decode(String str) {
        return new TypeFrame(TypeFrame.DATA, str.getBytes(charset));
    }
}
