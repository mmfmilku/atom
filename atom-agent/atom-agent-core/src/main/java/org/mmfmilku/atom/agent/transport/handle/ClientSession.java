package org.mmfmilku.atom.agent.transport.handle;

public interface ClientSession<T> {

    void send(T data);

    T read();

    T sendThenRead(T data);
    
    void close();

}
