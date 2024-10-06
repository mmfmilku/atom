package org.mmfmilku.atom.transport.frpc;

import org.mmfmilku.atom.transport.handle.FRPCHandle;
import org.mmfmilku.atom.transport.protocol.file.FServer;

import java.io.File;

public class FRPCStarter {

    public static final String F_SERVER_DIR = System.getProperty("user.dir") + File.separator + "fserver";

    public void runServer() {
        File baseDir = new File(F_SERVER_DIR);
        baseDir.delete();
        Thread thread = new Thread("frpc-thread") {
            @Override
            public void run() {
                FServer fServer = new FServer(F_SERVER_DIR).addHandle(new FRPCHandle());
                fServer.start();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

}
