package org.mmfmilku.atom.file;

import org.mmfmilku.atom.transport.protocol.client.ClientSession;
import org.mmfmilku.atom.transport.protocol.FClients;
import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;
import org.mmfmilku.atom.transport.protocol.client.FClient;
import org.mmfmilku.atom.transport.protocol.base.FServer;

import java.io.File;

public class FServerUtil {

    public static void runServer(ServerHandle ...serverHandles) {
        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
        FServer fServer = new FServer(path);
        for (ServerHandle serverHandle : serverHandles) {
            fServer.addHandle(serverHandle);
        }
        fServer.start();
    }

    public static void runServer(int maxConnect, ServerHandle ...serverHandles) {
        String path = System.getProperty("user.dir") + File.separator + "test" + File.separator + "transport";
        FServer fServer = new FServer(path, maxConnect);
        for (ServerHandle serverHandle : serverHandles) {
            fServer.addHandle(serverHandle);
        }
        fServer.start();
    }

    public static void runServer(String path, int maxConnect, ServerHandle ...serverHandles) {
        FServer fServer = new FServer(path, maxConnect);
        for (ServerHandle serverHandle : serverHandles) {
            fServer.addHandle(serverHandle);
        }
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
        return FClients.openStringClientSession(fClient);
    }

}
