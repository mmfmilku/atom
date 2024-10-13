package org.mmfmilku.atom.transport.frpc;

public interface Listener {

    void onFail(Throwable throwable);

    void onClose();

}
