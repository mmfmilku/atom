package org.mmfmilku.atom.transport.protocol.client;

public interface ClientSession<T> {

    void send(T data);

    T read();

    T sendThenRead(T data);
    
    void close();

}
