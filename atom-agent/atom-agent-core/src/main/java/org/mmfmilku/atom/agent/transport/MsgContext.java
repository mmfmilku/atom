package org.mmfmilku.atom.agent.transport;

import java.io.IOException;
import java.io.OutputStream;

public class MsgContext {

    private OutputStream outputStream;

    public MsgContext(OutputStream outputStream) {
        this.outputStream = outputStream;
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

}
