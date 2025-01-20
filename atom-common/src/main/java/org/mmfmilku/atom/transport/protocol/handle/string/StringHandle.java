package org.mmfmilku.atom.transport.protocol.handle.string;

import org.mmfmilku.atom.transport.protocol.handle.ChannelContext;
import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class StringHandle implements ServerHandle<TypeFrame, String> {

    private Charset charset = StandardCharsets.UTF_8;

    @Override
    public ChannelContext<String> getChannelContext(ChannelContext<TypeFrame> channelContext) {
        return str -> {
            TypeFrame typeFrame = new TypeFrame(TypeFrame.DATA, str.getBytes(charset));
            channelContext.write(typeFrame);
        };
    }

    @Override
    public void handle(TypeFrame typeFrame, Iterator<ServerHandle> handleItr, ChannelContext<TypeFrame> channelContext) {
        byte type = typeFrame.getType();
        if (TypeFrame.DATA == type) {
            byte[] data = typeFrame.getData();
            String dataStr = new String(data);
            handleNext(dataStr, handleItr, channelContext);
        }
    }
}
