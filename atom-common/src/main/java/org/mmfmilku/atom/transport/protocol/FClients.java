package org.mmfmilku.atom.transport.protocol;

import org.mmfmilku.atom.transport.protocol.client.FClient;
import org.mmfmilku.atom.transport.protocol.client.FClientSession;
import org.mmfmilku.atom.transport.protocol.client.StringClientSession;
import org.mmfmilku.atom.transport.protocol.client.TypeClientSession;

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

}
