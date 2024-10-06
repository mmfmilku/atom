package org.mmfmilku.atom.agent.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.mmfmilku.atom.agent.transport.ConnectContext;
import org.mmfmilku.atom.agent.transport.handle.ClientSession;
import org.mmfmilku.atom.agent.transport.handle.RRModeServerHandle;
import org.mmfmilku.atom.agent.transport.handle.SRClientSession;
import org.mmfmilku.atom.agent.transport.handle.ServerHandle;
import org.mmfmilku.atom.agent.transport.protocol.file.FClient;
import org.mmfmilku.atom.agent.transport.protocol.file.FServer;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TestUtil {

    public static String getJavaText(Class clazz) {
        String s = null;
        try {
            s = FileUtils.readText(System.getProperty("user.dir") +
                    "\\src\\test\\java\\" +
                    clazz.getName().replaceAll("\\.", "\\\\") +
                    ".java");
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return s;
    }

    @Test
    public void test() throws IOException {
        String javaText = getJavaText(this.getClass());
        String s = FileUtils.readText(System.getProperty("user.dir") + "\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\util\\TestUtil.java");
        assertEquals(javaText, s);
        System.out.println(javaText);
    }

    public static class FServerUtil {

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
}
