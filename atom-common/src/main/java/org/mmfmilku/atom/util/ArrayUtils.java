package org.mmfmilku.atom.util;

public class ArrayUtils {

    public static byte[] assembly(byte[] ...byteArrArr) {
        int resultLen = 0;
        for (byte[] byteArr : byteArrArr) {
            resultLen += byteArr.length;
        }
        byte[] allByte = new byte[resultLen];
        int addLen = 0;
        for (byte[] bytes : byteArrArr) {
            System.arraycopy(bytes, 0, allByte, addLen, bytes.length);
        }
        return allByte;
    }

}
