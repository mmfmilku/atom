package org.mmfmilku.atom.agent.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MsgContext {

    private InputStream inputStream;
    private OutputStream outputStream;
    
    private boolean close = false;

    public boolean isClose() {
        return close;
    }

    public MsgContext(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }
    
    public String read() {
        byte[] receiveLenByte = new byte[2];
        try {
            MessageUtils.readToFull(inputStream, receiveLenByte);
            // TODO 心跳处理
            int len = MessageUtils.decodeLength(receiveLenByte);
            if (len == 0) {
                // TODO 标识结束
                close();
                return null;
            }
            byte[] data = new byte[len];
            MessageUtils.readToFull(inputStream, data);
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
        return "";
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

    public void close() {
        System.out.println("关闭连接");
        write("");
        close = true;
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
