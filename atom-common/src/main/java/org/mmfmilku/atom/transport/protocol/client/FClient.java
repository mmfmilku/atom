package org.mmfmilku.atom.transport.protocol.client;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.client.ClientSession;
import org.mmfmilku.atom.transport.protocol.client.SRClientSession;
import org.mmfmilku.atom.transport.protocol.file.FServer;
import org.mmfmilku.atom.util.IOUtils;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * FClient
 *
 * @author mmfmilku
 * @date 2024/9/30:9:19
 */
public class FClient {

    // TODO 定时获取服务端消息
    private ScheduledExecutorService clientExecutor;

    private ConcurrentLinkedQueue<ClientSession> connectList;

    private String connectPath;

    public FClient(String connectPath) {
        this.connectPath = connectPath;
        init();
    }

    private void init() {
        this.connectList =  new ConcurrentLinkedQueue<>();
        this.clientExecutor = Executors.newScheduledThreadPool(1,
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
                                "pool-fclient-boss-thread-" + threadNumber.getAndIncrement(),
                                0);
                        t.setDaemon(true);
                        if (t.getPriority() != Thread.NORM_PRIORITY)
                            t.setPriority(Thread.NORM_PRIORITY);
                        return t;
                    }
                });

        clientExecutor.scheduleAtFixedRate(() -> {
            for (ClientSession clientSession : connectList) {
                // TODO 定时读取
                // TODO 并发控制
            }
            // 300ms执行一次
        }, 0, 300, TimeUnit.MILLISECONDS);
    }

    public ClientSession<String> connect() {
        String uuid = UUID.randomUUID().toString();
        return connect(uuid);
    }
    
    public ClientSession<String> connect(String name) {
        checkListen();
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
        waitCount(o -> responseFile.exists());
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(responseFile);
            outputStream = new FileOutputStream(requestFile);
            Connector ctx = new Connector(inputStream, outputStream, null);
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

    private void checkListen() {
        File connectDir = new File(connectPath);
        waitCount(o -> connectDir.exists());
    }

    private void waitCount(Function<Integer, Boolean> function) {
        int waitCount = 0;
        while (!function.apply(waitCount)) {
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
        }
    }

}
