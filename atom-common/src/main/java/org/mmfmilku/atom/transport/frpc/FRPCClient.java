package org.mmfmilku.atom.transport.frpc;

import org.mmfmilku.atom.transport.client.ClientSession;
import org.mmfmilku.atom.transport.protocol.file.FClient;
import org.mmfmilku.atom.util.IOUtils;

import java.util.Base64;

public class FRPCClient {

    private FClient fClient;
    private ClientSession<String> connect;

    public FRPCClient() {
        fClient = new FClient(FRPCStarter.F_SERVER_DIR);
        connect = fClient.connect();
    }

    public FRPCReturn call(FRPCParam frpcParam) {
        byte[] serialize = IOUtils.serialize(frpcParam);
        String toSend = Base64.getEncoder().encodeToString(serialize);
        String read = connect.sendThenRead(toSend + "\r");
        byte[] decode = Base64.getDecoder().decode(read.trim());
        return IOUtils.deserialize(decode);
    }

}
