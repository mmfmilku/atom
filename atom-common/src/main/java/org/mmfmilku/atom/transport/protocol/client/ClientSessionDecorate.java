package org.mmfmilku.atom.transport.protocol.client;

public abstract class ClientSessionDecorate<A, B> implements ClientSession<B> {

    private ClientSession<A> clientSession;

    public ClientSessionDecorate(ClientSession<A> clientSession) {
        this.clientSession = clientSession;
    }

    protected abstract B code(A a);

    protected abstract A decode(B b);

    @Override
    public void send(B data) {
        clientSession.send(decode(data));
    }

    @Override
    public B read() {
        return code(clientSession.read());
    }

    @Override
    public B sendThenRead(B data) {
        send(data);
        return read();
    }

    @Override
    public void close() {
        clientSession.close();
    }
}
