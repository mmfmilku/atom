package org.mmfmilku.atom.transport.protocol.frame;

/**
 * 基础通信帧，负责基础的数据传输
 * 2    +  data        =   2 ~ 65538
 * 长度    内容(最大65535)
 * */
public class FFrame {

    /**
     * 最大值 0xffff=65535
     * */
//    private int len;
    private byte[] len;

//    /**
//     * 报文类型
//     * 0-心跳报文
//     * 1-建立连接
//     * 2-关闭连接
//     * 3-业务报文
//     * 4-错误报文
//     * */
//    private byte type;

    private byte[] data;

    public byte[] getLen() {
        return len;
    }

    public void setLen(byte[] len) {
        this.len = len;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
