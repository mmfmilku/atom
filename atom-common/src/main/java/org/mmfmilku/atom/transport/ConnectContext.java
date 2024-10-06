package org.mmfmilku.atom.transport;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

public class ConnectContext implements Closeable {

    private InputStream inputStream;
    private OutputStream outputStream;
    private Consumer<ConnectContext> closeCallback;
    private long readTimeout = 1000;
    
    private boolean close = false;

    public boolean isClose() {
        return close;
    }

    public ConnectContext(InputStream inputStream, OutputStream outputStream, Consumer<ConnectContext> closeCallback) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.closeCallback = closeCallback;
    }

    private byte[] preRead = null;

    public boolean canRead() {
        if (preRead != null) {
            return true;
        }
        byte[] tmpRead = new byte[1];
        try {
            int read = inputStream.read(tmpRead);
            if (read > 0) {
                // TODO 并发？,canRead方法与read方法不能并发执行
                preRead = tmpRead;
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String read() {
        long timeoutTimeMillis = System.currentTimeMillis() + readTimeout;
        byte[] receiveLenByte = new byte[2];
        try {
            if (preRead != null) {
                // 读取过第一个字节
                byte[] tmpByte = new byte[1];
                readToFull(tmpByte, timeoutTimeMillis);
                receiveLenByte[0] = preRead[0];
                receiveLenByte[1] = tmpByte[0];
                preRead = null;
            } else {
                readToFull(receiveLenByte, timeoutTimeMillis);
            }
            // TODO 心跳处理
            int len = MessageUtils.decodeLength(receiveLenByte);
            if (len == 0) {
                // TODO 标识结束
                close();
                return null;
            }
            byte[] data = new byte[len];
            readToFull(data, timeoutTimeMillis);
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
        return "";
    }

    private void readToFull(byte[] buf, long timeoutTimeMillis) throws IOException {
        int read;
        // 剩余读取大小，初始为数组大小
        int left = buf.length;
        // 读取偏移量，初始为0
        int off = 0;
        while (left > 0 &&
                ((read = inputStream.read(buf, off, left)) != left)) {
            // TODO 设置读取超时
            if (read <= 0) {
                if (System.currentTimeMillis() > timeoutTimeMillis) {
                    // 读取超时
                    throw new RuntimeException("读取超时");
                }
                continue;
            }
            left -= read;
            off = buf.length - left;
        }
    }

    public void write(String send) {
        byte[] bytes = send.getBytes();
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
        write("");
        close = true;
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
    }

}
