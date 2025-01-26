package org.mmfmilku.atom.transport.protocol.handle;


/**
 * 请求响应模式
 * */
public interface RRModeHandle<IN> extends ServerHandle<IN, IN> {

    @Override
    default void handle(IN in, PipeLine pipeLine) {
        onReceive(in, pipeLine);
    }

    void onReceive(IN in, PipeLine pipeLine);

    @Override
    default IN code(IN in) {
        return in;
    }

    @Override
    default IN decode(IN in) {
        return in;
    }
}
