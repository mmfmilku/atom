package org.mmfmilku.atom.agent.transport;

import java.io.IOException;
import java.io.InputStream;

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

    public static void readToFull(InputStream inputStream, byte[] buf) throws IOException {
        int read;
        // 剩余读取大小，初始为数组大小
        int left = buf.length;
        // 读取偏移量，初始为0
        int off = 0;
        int waitCount = 0;
        while (left > 0 &&
                ((read = inputStream.read(buf, off, left)) != left)) {
            // TODO 设置读取超时
            if (waitCount > 10) {

            }
            if (read <= 0) {
                waitCount++;
                continue;
            }
            waitCount = 0;
            left -= read;
            off = buf.length - left;
        }
    }

}
