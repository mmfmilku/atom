package org.mmfmilku.atom.transport.protocol.handle.type;

public class BigTypeFrame extends TypeFrame {

    /**
     * 5-大数据
     * */
    public static final byte BIG_DATA = 5;

    public BigTypeFrame(byte[] data) {
        super(data);
    }
}
