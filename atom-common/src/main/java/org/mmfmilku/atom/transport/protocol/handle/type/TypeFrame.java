package org.mmfmilku.atom.transport.protocol.handle.type;

public class TypeFrame {

    /**
     * 报文类型
     * 0-心跳报文
     * 1-建立连接
     * 2-关闭连接
     * 3-业务报文
     * 4-错误报文
     * */
    public static final byte HEARTBEAT = 0;
    public static final byte OPEN = 1;
    public static final byte CLOSE = 2;
    public static final byte DATA = 3;
    public static final byte ERROR = 4;

    private byte type;

    private byte[] data;

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
