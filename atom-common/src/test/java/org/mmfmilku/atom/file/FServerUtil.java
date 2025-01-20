package org.mmfmilku.atom.file;

import org.mmfmilku.atom.transport.protocol.client.ClientSession;
import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;
import org.mmfmilku.atom.transport.protocol.client.FClient;
import org.mmfmilku.atom.transport.protocol.base.FServer;

public class FServerUtil {

    public static void runServer(ServerHandle serverHandle) {
        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
        FServer fServer = new FServer(path).addHandle(serverHandle);
        fServer.start();
    }

    public static void runServerDaemon(ServerHandle serverHandle) {
        Thread thread = new Thread("test-fserver") {
            @Override
            public void run() {
                FServerUtil.runServer(serverHandle);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public static ClientSession<String> connect() {
        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
        FClient fClient = new FClient(path);
        return fClient.connect();
    }

}
