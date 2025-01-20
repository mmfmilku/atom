package org.mmfmilku.atom.transport.protocol.handle;

import org.mmfmilku.atom.transport.protocol.exception.ConnectException;

import java.util.Iterator;

/**
 * 请求响应模式
 * */
public interface RRModeHandle<IN> extends ServerHandle<IN, IN> {

    @Override
    default ChannelContext<IN> getChannelContext(ChannelContext<IN> channelContext) {
        throw new ConnectException("not support");
    }

    @Override
    default void handle(IN in, Iterator<ServerHandle> handleItr, ChannelContext<IN> channelContext) {
        onReceive(in, channelContext);
    }

    void onReceive(IN in, ChannelContext<IN> channelContext);
}
