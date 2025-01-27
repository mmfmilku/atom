package org.mmfmilku.atom.transport.protocol.client;

import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.handle.Codec;
import org.mmfmilku.atom.transport.protocol.handle.assembly.AssemblyCodec;
import org.mmfmilku.atom.transport.protocol.handle.assembly.AssemblyDataFrame;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * StringClientSession
 * Type-String协议
 *
 * @author chenxp
 * @date 2024/9/30:13:40
 */
public class AssemblyClientSession implements ClientSession<TypeFrame> {

    private ClientSession<TypeFrame> clientSession;

    private static Codec<List<TypeFrame>, AssemblyDataFrame> codec = new AssemblyCodec();

    public AssemblyClientSession(ClientSession<TypeFrame> clientSession) {
        this.clientSession = clientSession;
    }

    @Override
    public void send(TypeFrame data) {
        List<TypeFrame> typeFrames = codec.decode(new AssemblyDataFrame(data.getData()));
        for (TypeFrame typeFrame : typeFrames) {
            clientSession.send(typeFrame);
        }
    }

    @Override
    public AssemblyDataFrame read() {
        TypeFrame typeFrame = clientSession.read();
        byte[] data = typeFrame.getData();
        int total = MessageUtils.decodeInt(Arrays.copyOfRange(data, 0, 2));
        List<TypeFrame> typeFrames = new ArrayList<>(total);
        typeFrames.add(typeFrame);
        for (int i = 2; i <= total; i++) {
            typeFrames.add(clientSession.read());
        }
        return codec.code(typeFrames);
    }

    @Override
    public AssemblyDataFrame sendThenRead(TypeFrame data) {
        send(data);
        return read();
    }

    @Override
    public void close() {
        clientSession.close();
    }
}
