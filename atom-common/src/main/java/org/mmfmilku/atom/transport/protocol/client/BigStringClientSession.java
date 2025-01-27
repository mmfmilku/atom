package org.mmfmilku.atom.transport.protocol.client;

import org.mmfmilku.atom.transport.protocol.handle.assembly.AssemblyDataFrame;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * StringClientSession
 * Type-String协议
 *
 * @author chenxp
 * @date 2024/9/30:13:40
 */
public class BigStringClientSession extends ClientSessionDecorate<AssemblyDataFrame, String>
        implements ClientSession<String> {

    private Charset charset;

    public BigStringClientSession(ClientSession<AssemblyDataFrame> clientSession) {
        super(clientSession);
        charset = StandardCharsets.UTF_8;
    }

    @Override
    protected String code(AssemblyDataFrame typeFrame) {
        return new String(typeFrame.getData());
    }

    @Override
    protected AssemblyDataFrame decode(String s) {
        return new AssemblyDataFrame(s.getBytes(charset));
    }

}
