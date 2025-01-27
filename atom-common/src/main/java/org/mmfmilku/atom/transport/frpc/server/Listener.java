package org.mmfmilku.atom.transport.frpc.server;

public interface Listener {

    void onFail(Throwable throwable);

    void onClose();

}
