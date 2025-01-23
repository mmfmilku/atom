package org.mmfmilku.atom.transport.protocol.client;

public class FClients {

    public static FClientSession getFClientSession(FClient fClient) {
        return fClient.connect();
    }

    public static TypeClientSession getTypeClientSession(FClient fClient) {
        return new TypeClientSession(getFClientSession(fClient));
    }

    public static StringClientSession getStringClientSession(FClient fClient) {
        return new StringClientSession(getTypeClientSession(fClient));
    }

}
