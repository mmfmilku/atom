package org.mmfmilku.atom.file;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mmfmilku.atom.transport.protocol.FClients;
import org.mmfmilku.atom.transport.protocol.client.BigStringClientSession;
import org.mmfmilku.atom.transport.protocol.client.FClient;
import org.mmfmilku.atom.transport.protocol.client.TypeClientSession;
import org.mmfmilku.atom.transport.protocol.exception.MessageCodeException;
import org.mmfmilku.atom.transport.protocol.handle.RRModeHandle;
import org.mmfmilku.atom.transport.protocol.handle.string.StringHandle;
import org.mmfmilku.atom.transport.protocol.handle.assembly.TypeAssemblyHandler;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeHandler;

import java.io.File;

import static org.junit.Assert.*;

public class BigDataTest {

    private static String path1 = System.getProperty("user.dir") + File.separator + "test" + File.separator + "smallData";
    private static String path2 = System.getProperty("user.dir") + File.separator + "test" + File.separator + "bigData";

    private FClient fClient1 = new FClient(path1);
    private FClient fClient2 = new FClient(path2);

    @BeforeClass
    public static void beforeClass() {
        FServerUtil.runServer(path1, 1,
                new TypeHandler(), new StringHandle(),
                (RRModeHandle<String>) (data, pipeLine) ->
                        pipeLine.write( data));
        FServerUtil.runServer(path2, 2,
                new TypeHandler(), new TypeAssemblyHandler(), new StringHandle(),
                (RRModeHandle<String>) (data, pipeLine) ->
                        pipeLine.write( "big:" + data));
    }

    @Test
    public void smallServerCheck() {
        TypeClientSession session = FClients.openTypeClientSession(fClient1);
        session.sendThenRead(new TypeFrame(new byte[65534]));
        try {
            TypeFrame typeFrame = session.sendThenRead(new TypeFrame(new byte[65535]));
            fail("data too long check");
        } catch (MessageCodeException e) {
            assertEquals(e.getMessage(), "data too long 65536");
        }
    }

    @Test
    public void bigServerTest() {
        BigStringClientSession session = FClients.openBigStringSession(fClient2);
        String s = "aaa\r";
        assertEquals("big:" + s, session.sendThenRead(s));

        StringBuilder big = new StringBuilder();

        checkBigFrame(session, big, 100);
        checkBigFrame(session, big, 1000);
        checkBigFrame(session, big, 10_000);
        checkBigFrame(session, big, 100_000);
    }

    private void checkBigFrame(BigStringClientSession session, StringBuilder big, int len) {
        String read;
        big.setLength(0);
        big.append("start-");
        for (int i = 0; i < len; i++) {
            big.append(i);
        }
        big.append("\r");
        read = session.sendThenRead(big.toString());
        assertEquals("big:" + big.toString(), read);
    }

}
