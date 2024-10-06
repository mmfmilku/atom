package org.mmfmilku.atom.transport.protocol.file;

import org.mmfmilku.atom.transport.ConnectContext;
import org.mmfmilku.atom.transport.handle.ClientSession;
import org.mmfmilku.atom.transport.handle.SRClientSession;
import org.mmfmilku.atom.util.IOUtils;

import java.io.*;
import java.util.UUID;

/**
 * FClient
 *
 * @author chenxp
 * @date 2024/9/30:9:19
 */
public class FClient {

    private String connectPath;

    public FClient(String connectPath) {
        this.connectPath = connectPath;
    }

    public ClientSession<String> connect() {
        String uuid = UUID.randomUUID().toString();
        return connect(uuid);
    }
    
    public ClientSession<String> connect(String name) {
        String requestName = name + FServer.REQUEST;
        String responseName = requestName + FServer.RESPONSE;
        File requestFile = new File(connectPath, requestName);
        if (requestFile.exists() || requestFile.isDirectory()) {
            throw new RuntimeException("连接失败,连接重复");
        }
        try {
            boolean newFile = requestFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("连接失败");
        }
        File responseFile = new File(connectPath, responseName);
        responseFile = waitCount(responseName, responseFile);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(responseFile);
            outputStream = new FileOutputStream(requestFile);
            ConnectContext ctx = new ConnectContext(inputStream, outputStream, null);
            // TODO @chenxp 2024/9/30 建立连接
            String read = ctx.read();
            if ("PING".equals(read)) {
                ctx.write("PONG");
            } else {
                throw new RuntimeException("连接失败");
            }
            return new SRClientSession(ctx);
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
        throw new RuntimeException("连接失败");
    }

    private File waitCount(String responseName, File responseFile) {
        int waitCount = 0;
        while (!responseFile.exists()) {
            if (waitCount > 10) {
                throw new RuntimeException("等待服务器响应超时");
            }
            try {
                System.out.println("等待服务器响应");
                Thread.sleep(1000);
                waitCount++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            responseFile = new File(connectPath, responseName);
        }
        return responseFile;
    }

}
