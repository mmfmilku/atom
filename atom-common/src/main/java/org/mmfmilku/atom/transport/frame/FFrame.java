package org.mmfmilku.atom.transport.frame;

/**
 * 基础通信帧
 * 2    +   1 +     data        =   3 ~ 65538
 * 长度    类型     内容(最大65535)
 * */
public class FFrame {

    /**
     * 最大值 0xffff=65535
     * */
    private int len;

    /**
     * 报文类型
     * 0-心跳报文
     * 1-建立连接
     * 2-关闭连接
     * 3-业务报文
     * 4-错误报文
     * */
    private byte type;

    private byte[] data;
}
