package org.mmfmilku.atom.transport.handle;

public interface ClientSession<T> {

    void send(T data);

    T read();

    T sendThenRead(T data);
    
    void close();

}
