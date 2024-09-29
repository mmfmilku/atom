package org.mmfmilku.atom.agent.transport.protocol.file;

import org.junit.Test;
import org.mmfmilku.atom.agent.transport.MessageUtils;

import static org.junit.Assert.*;

public class FServerTest {

    @Test
    public void testStart() {
        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
        FServer fServer = new FServer(path);
        fServer.start();
    }

    @Test
    public void testLength() {
        for (int i = 0; i <= 65535; i+=1) {
            byte[] bytes = MessageUtils.codeLength(i);
            int i1 = MessageUtils.decodeLength(bytes);
            assertEquals(i, i1);
        }
        try {
            MessageUtils.codeLength(65536);
            fail("65536长度超长未校验");
        } catch (Exception ignored) {

        }
    }

}