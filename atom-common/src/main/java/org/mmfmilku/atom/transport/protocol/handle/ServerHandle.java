package org.mmfmilku.atom.transport.protocol.handle;

import java.util.Iterator;

public interface ServerHandle<IN, OUT> {

    ChannelContext<OUT> getChannelContext(ChannelContext<IN> channelContext);

    void handle(IN in, Iterator<ServerHandle> handleItr, ChannelContext<IN> channelContext);

    default void onOpen(ChannelContext<IN> channelContext) {}

    default void onOpenFail(ChannelContext<IN> channelContext) {}

    default void beforeClose(ChannelContext<IN> channelContext) {}

    default void afterClose(ChannelContext<IN> channelContext) {}

    default void onError(ChannelContext<IN> channelContext) {}

    default void handleNext(Object frame, Iterator<ServerHandle> handleItr, ChannelContext channelContext) {
        if (handleItr.hasNext()) {
            ServerHandle next = handleItr.next();
            next.handle(frame, handleItr, getChannelContext(channelContext));
        }
    }

}
