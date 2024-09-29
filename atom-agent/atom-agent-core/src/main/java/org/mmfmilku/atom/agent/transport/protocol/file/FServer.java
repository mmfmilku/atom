/********************************************
 * 文件名称: Server.java
 * 系统名称: 综合理财管理平台6.0
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明:
 * 系统版本: 6.0.0.1
 * 开发人员: chenxp
 * 开发时间: 2024/9/29
 * 审核人员:
 * 相关文档:
 * 修改记录:   修改日期    修改人员    修改单号       版本号                   修改说明
 * V6.0.0.1  20240929-01  chenxp   TXXXXXXXXXXXX    IFMS6.0VXXXXXXXXXXXXX   新增 
 *********************************************/
package org.mmfmilku.atom.agent.transport.protocol.file;

import org.mmfmilku.atom.agent.transport.MessageUtils;
import org.mmfmilku.atom.agent.transport.MsgContext;
import org.mmfmilku.atom.agent.transport.handle.Handle;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Server
 *
 * @author chenxp
 * @date 2024/9/29:16:49
 */
public class FServer {

    public static final String REQUEST = ".request";

    public static final String RESPONSE = ".response";

    private String listenPath;
    
    private Set<String> accepted = new HashSet<>();

    public FServer(String listenPath) {
        this.listenPath = listenPath;
    }

    public void start() {
        File listen = new File(listenPath);
        if (!listen.exists()) {
            boolean mkdirs = listen.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException("启动失败,创建目录失败");
            }
        } else if (listen.isFile()) {
            throw new RuntimeException("启动失败,监听路径需为文件夹");
        }
        while (true) {
            // TODO @chenxp 2024/9/29 每行的数据完整读取
            File[] requestFiles = listen.listFiles((dir, name) -> !accepted.contains(name) 
                    && name.endsWith(REQUEST));
            if (requestFiles != null) {
                for (File requestFile : requestFiles) {
                    accept(requestFile);
                }
            }
            // TODO 配置超时时间
            if (true) {
                break;
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void accept(File requestFile) {
        try (InputStream inputStream = new FileInputStream(requestFile);
             OutputStream outputStream = new FileOutputStream(
                     new File(requestFile.getAbsolutePath() + RESPONSE))) {
            accepted.add(requestFile.getName());
            service(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handle handle = (data, ctx) -> {
        String input = new String((byte[]) data);
        System.out.println("收到数据:" + input);
        ctx.write("你好,收到你的数据了\n");
        ctx.write("完毕\n");
    };

    private void service(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] receiveLenByte = new byte[2];
        MsgContext ctx = new MsgContext(outputStream);
        while (true) {
            readToFull(inputStream, receiveLenByte);
            // TODO 心跳处理
            int len = MessageUtils.decodeLength(receiveLenByte);
            if (len == 0) {
                // TODO 标识结束
                ctx.write("");
                return;
            }
            byte[] data = new byte[len];
            readToFull(inputStream, data);
            handle.receive(data, ctx);
        }
    }

    private void readToFull(InputStream inputStream, byte[] buf) throws IOException {
        int read;
        // 剩余读取大小，初始为数组大小
        int left = buf.length;
        // 读取偏移量，初始为0
        int off = 0;
        while (left > 0 &&
                ((read = inputStream.read(buf, off, left)) != left)) {
            // TODO 设置读取超时
            if (read <= 0) {
                continue;
            }
            left -= read;
            off = buf.length - left;
        }
    }

}


