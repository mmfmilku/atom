package org.mmfmilku.atom.transport;

public class MessageUtils {

    private static int MAX_BYTE_2 = 0xffff;

    public static byte[] codeLength(int i) {
        if (i > MAX_BYTE_2) {
            throw new RuntimeException("长度超长" + i);
        }
        byte b1 = (byte) (i >> 8);
        byte b2 = (byte) (i & 0xff);
        return new byte[]{b1, b2};
    }

    public static int decodeLength(byte[] byteBuf) {
        if (byteBuf.length != 2) {
            throw new RuntimeException("字节数组大小不为2");
        }
        byte b1 = byteBuf[0];
        byte b2 = byteBuf[1];
        return ((((int) b1) & 0xff) << 8) + (((int) b2) & 0xff);
    }

}
