package org.mmfmilku.atom.transport.protocol;

import org.mmfmilku.atom.transport.protocol.exception.ConnectException;
import org.mmfmilku.atom.transport.protocol.base.FFrame;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;

public class Connector implements Closeable {

    private InputStream inputStream;
    private OutputStream outputStream;
    private Consumer<Connector> closeCallback;

    private boolean close = false;

    private Charset charset = StandardCharsets.UTF_8;

    public boolean isClose() {
        return close;
    }

    public Connector(InputStream inputStream, OutputStream outputStream, Consumer<Connector> closeCallback) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.closeCallback = closeCallback;
    }

    // 长度数据
    private final byte[] tmpLen = new byte[2];
    // 已读的长度
    private volatile int lenReadSize = 0;

    // 内容数据
    private volatile byte[] tmpData = null;
    // 已读的内容长度
    private volatile int dataReadSize = 0;

    // 非线程安全！
    public FFrame tryRead() {
        try {
            if (lenReadSize != tmpLen.length) {
                // 读取长度数据
                int read = inputStream.read(tmpLen, lenReadSize, tmpLen.length - lenReadSize);
                if (read < 1) {
                    return null;
                }
                lenReadSize += read;
                if (lenReadSize != tmpLen.length) {
                    // 长度数据不完整，读取失败返回
                    return null;
                } else {
                    // 重置上一次读取的数据
                    tmpData = null;
                    dataReadSize = 0;
                }
            }
            int len = MessageUtils.decodeLength(tmpLen);
            if (tmpData == null) {
                tmpData = new byte[len];
                dataReadSize = 0;
            }
            assert tmpData.length == len;
            if (dataReadSize != tmpData.length) {
                int read = inputStream.read(tmpData, dataReadSize, tmpData.length - dataReadSize);
                if (read < 1) {
                    return null;
                }
                dataReadSize += read;
                if (dataReadSize != tmpData.length) {
                    return null;
                }
            }
            // 下一次数据帧从头开始读取
            lenReadSize = 0;
            FFrame fFrame = new FFrame();
            // 复制数据，避免后续修改影响
            fFrame.setLen(Arrays.copyOf(tmpLen, tmpLen.length));
            fFrame.setData(Arrays.copyOf(tmpData, tmpData.length));
            return fFrame;
        } catch (IOException e) {
            e.printStackTrace();
            close();
            throw new ConnectException(e);
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    public FFrame read(long readTimeOutMillis) {
        long timeOut = System.currentTimeMillis() + readTimeOutMillis;
        do {
            // 至少读取一次
            FFrame fFrame = tryRead();
            if (fFrame != null) {
                return fFrame;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (timeOut > System.currentTimeMillis());
        return null;
    }

    public void write(FFrame fFrame) {
        try {
            outputStream.write(fFrame.getLen());
            outputStream.write(fFrame.getData());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectException(e);
        }
    }

    @Deprecated
    public void write(String send) {
        byte[] bytes = send.getBytes(charset);
        byte[] sendLenByte = MessageUtils.codeLength(bytes.length);
        try {
            outputStream.write(sendLenByte);
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        System.out.println("关闭连接");
        write(MessageUtils.packFFrame());
        if (closeCallback != null) {
            closeCallback.accept(this);
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        close = true;
    }

}
