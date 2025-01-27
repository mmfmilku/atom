package org.mmfmilku.atom.transport.protocol;

import org.mmfmilku.atom.transport.protocol.client.*;

public class FClients {

    public static FClient newFClient(String connectPath) {
        return new FClient(connectPath);
    }

    public static FClientSession openFClientSession(FClient fClient) {
        return fClient.connect();
    }

    public static TypeClientSession openTypeClientSession(FClient fClient) {
        return new TypeClientSession(openFClientSession(fClient));
    }

    public static StringClientSession openStringClientSession(FClient fClient) {
        return new StringClientSession(openTypeClientSession(fClient));
    }

    public static BigStringClientSession openBigStringSession(FClient fClient) {
        return new BigStringClientSession(new AssemblyClientSession(openTypeClientSession(fClient)));
    }

}
