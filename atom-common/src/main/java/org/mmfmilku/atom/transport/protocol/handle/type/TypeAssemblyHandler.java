package org.mmfmilku.atom.transport.protocol.handle.type;

import org.mmfmilku.atom.transport.protocol.handle.ChannelContext;
import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;

import java.util.Iterator;

public class TypeAssemblyHandler implements ServerHandle<TypeFrame, TypeFrame> {

    @Override
    public ChannelContext<TypeFrame> getChannelContext(ChannelContext<TypeFrame> channelContext) {
        // TODO 拆分数据
        return null;
    }

    @Override
    public void handle(TypeFrame typeFrame, Iterator<ServerHandle> handleItr, ChannelContext<TypeFrame> channelContext) {
        if (BigTypeFrame.BIG_DATA == typeFrame.getType()) {
            byte[] data = typeFrame.getData();
            // TODO 长度超长，组合数据

        } else {
            handleNext(typeFrame, handleItr, channelContext);
        }
    }
}
