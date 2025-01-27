package org.mmfmilku.atom.file;

import org.junit.Test;
import org.mmfmilku.atom.transport.protocol.MessageUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FServerTest {

    @Test
    public void testLength() {
        for (int i = 0; i <= 65535; i+=1) {
            byte[] bytes = MessageUtils.codeInt(i);
            int i1 = MessageUtils.decodeInt(bytes);
            assertEquals(i, i1);
        }
        try {
            MessageUtils.codeInt(65536);
            fail("65536长度超长未校验");
        } catch (Exception ignored) {

        }
    }

    @Test
    public void testPack() {
        long l = System.currentTimeMillis();
        System.out.println(l);
        System.out.println((byte)l);
    }

}