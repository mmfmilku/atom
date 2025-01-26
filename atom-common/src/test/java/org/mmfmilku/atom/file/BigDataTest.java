package org.mmfmilku.atom.file;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mmfmilku.atom.transport.protocol.FClients;
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
                (RRModeHandle<String>) (data, channelContext) ->
                        channelContext.write( data));
        FServerUtil.runServer(path2, 2,
                new TypeHandler(), new TypeAssemblyHandler(), new StringHandle(),
                (RRModeHandle<String>) (data, channelContext) ->
                        channelContext.write( "big:" + data));
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
        TypeClientSession session = FClients.openTypeClientSession(fClient2);
        String base = new String(new byte[65534]);
        TypeFrame typeFrame;
        typeFrame = session.sendThenRead(new TypeFrame(base.getBytes()));
        assertEquals("big:" + base, new String(typeFrame.getData()));
        typeFrame = session.sendThenRead(new TypeFrame((base + base).getBytes()));
        assertEquals("big:" + base + base, new String(typeFrame.getData()));
        
        StringBuilder big = new StringBuilder();
        for (int i = 0; i < 1000_000; i++) {
            big.append(i + "");
        }
        typeFrame = session.sendThenRead(new TypeFrame(big.toString().getBytes()));
        assertEquals("big:" + big.toString(), new String(typeFrame.getData()));
    }

}
