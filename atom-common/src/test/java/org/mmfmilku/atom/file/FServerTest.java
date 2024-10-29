package org.mmfmilku.atom.file;

import org.junit.Test;
import org.mmfmilku.atom.transport.MessageUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FServerTest {

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