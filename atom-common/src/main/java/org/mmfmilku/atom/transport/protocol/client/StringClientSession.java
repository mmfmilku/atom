package org.mmfmilku.atom.transport.protocol.client;

import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * StringClientSession
 * Type-String协议
 *
 * @author chenxp
 * @date 2024/9/30:13:40
 */
public class StringClientSession extends ClientSessionDecorate<TypeFrame, String>
        implements ClientSession<String> {

    private Charset charset;

    public StringClientSession(ClientSession<TypeFrame> clientSession) {
        super(clientSession);
        charset = StandardCharsets.UTF_8;
    }

    @Override
    protected String code(TypeFrame typeFrame) {
        return new String(typeFrame.getData());
    }

    @Override
    protected TypeFrame decode(String s) {
        return new TypeFrame(TypeFrame.DATA, s.getBytes(charset));
    }

}
