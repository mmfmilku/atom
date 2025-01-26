package org.mmfmilku.atom.transport.protocol.client;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.base.FFrame;
import org.mmfmilku.atom.transport.protocol.base.FServer;
import org.mmfmilku.atom.transport.protocol.exception.ConnectException;
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

    // 读取完整帧超时时间，毫秒
    private long readTimeOutMillis;

    public FClient(String connectPath) {
        this.connectPath = connectPath;
        this.readTimeOutMillis = 2000;
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

    public FClientSession connect() {
        String uuid = UUID.randomUUID().toString();
        return connect(uuid);
    }
    
    public FClientSession connect(String name) {
        System.out.println("请求连接" + name);
        checkListen();
        String requestName = name + FServer.REQUEST;
        String responseName = requestName + FServer.RESPONSE;
        File requestFile = new File(connectPath, requestName);
        if (requestFile.exists() || requestFile.isDirectory()) {
            throw new ConnectException("连接失败,连接重复");
        }
        try {
            boolean newFile = requestFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectException("连接失败");
        }
        File responseFile = new File(connectPath, responseName);
        waitCount(o -> responseFile.exists());
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(responseFile);
            outputStream = new FileOutputStream(requestFile);
            Connector ctx = new Connector(inputStream, outputStream, null);
            FFrame ping = ctx.read(readTimeOutMillis);
            if (ping != null) {
                if (MessageUtils.decodeInt(ping.getLen()) == 0) {
                    throw new ConnectException("连接已满");
                }
                byte[] pong = MessageUtils.getPong(ping.getData());
                ctx.write(MessageUtils.packFFrame(pong));
                FFrame accept = ctx.read(readTimeOutMillis);
                if (accept != null && accept.getData()[0] == 1) {
                    return new FClientSession(ctx);
                } else {
                    throw new ConnectException("连接失败");
                }
            } else {
                throw new ConnectException("连接超时");
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
        throw new ConnectException("连接失败");
    }

    private void checkListen() {
        File connectDir = new File(connectPath);
        waitCount(o -> connectDir.exists());
    }

    private void waitCount(Function<Integer, Boolean> function) {
        int waitCount = 0;
        while (!function.apply(waitCount)) {
            if (waitCount > 10) {
                throw new ConnectException("等待服务器响应超时");
            }
            try {
//                System.out.println("等待服务器响应");
                Thread.sleep(300);
                waitCount++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
