package org.mmfmilku.atom.transport.protocol;

import org.mmfmilku.atom.transport.protocol.base.FFrame;
import org.mmfmilku.atom.transport.protocol.exception.MessageCodeException;

import java.util.Arrays;

public class MessageUtils {

    private static int MAX_BYTE_2 = 0xffff;

    public static byte[] codeInt(int i) {
        if (i > MAX_BYTE_2) {
            throw new MessageCodeException("data too long " + i);
        }
        byte b1 = (byte) (i >> 8);
        byte b2 = (byte) (i & 0xff);
        return new byte[]{b1, b2};
    }

    public static int decodeInt(byte[] byteBuf) {
        if (byteBuf.length != 2) {
            throw new RuntimeException("字节数组大小不为2");
        }
        byte b1 = byteBuf[0];
        byte b2 = byteBuf[1];
        return ((((int) b1) & 0xff) << 8) + (((int) b2) & 0xff);
    }

    public static FFrame packFFrame(byte ...data) {
        byte[] lenByte = MessageUtils.codeInt(data.length);
        FFrame fFrame = new FFrame();
        fFrame.setData(data);
        fFrame.setLen(lenByte);
        return fFrame;
    }

    public static FFrame[] getPingPong() {
        byte[] ping = getPing();
        byte[] pong = getPong(ping);
        return new FFrame[]{MessageUtils.packFFrame(ping),
                MessageUtils.packFFrame(pong)};
    }

    public static byte[] getPing() {
        long l = System.currentTimeMillis();
        return new byte[]{(byte) l};
    }

    public static byte[] getPong(byte[] ping) {
        byte[] pong = new byte[ping.length];
        for (int i = 0; i < ping.length; i++) {
            pong[i] = (byte) (ping[i] >> 1);
        }
        return pong;
    }

    public static boolean equals(FFrame a, FFrame b) {
        if (a == b)
            return true;
        if (a == null || b == null)
            return false;
        return Arrays.equals(a.getData(), b.getData());
    }

}
