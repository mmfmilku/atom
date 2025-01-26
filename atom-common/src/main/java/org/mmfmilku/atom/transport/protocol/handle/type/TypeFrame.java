package org.mmfmilku.atom.transport.protocol.handle.type;

public class TypeFrame {

    /**
     * 0-心跳报文
     * */
    public static final byte HEARTBEAT = 0;
    /**
     * 1-建立连接
     * */
    public static final byte OPEN = 1;
    /**
     * 2-关闭连接
     * */
    public static final byte CLOSE = 2;
    /**
     * 3-业务报文
     * */
    public static final byte DATA = 3;
    /**
     * 4-错误报文
     * */
    public static final byte ERROR = 4;

    public static final int MAX_DATA_LENGTH = 65534;

    private byte type;

    // max length 65534
    private byte[] data;

    public TypeFrame(byte[] data) {
        this.type = DATA;
        this.data = data;
    }

    public TypeFrame(byte type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
