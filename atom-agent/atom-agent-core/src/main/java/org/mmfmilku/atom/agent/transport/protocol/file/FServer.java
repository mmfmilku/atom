package org.mmfmilku.atom.agent.transport.protocol.file;

import org.mmfmilku.atom.agent.transport.MsgContext;
import org.mmfmilku.atom.agent.transport.handle.BaseServerHandle;
import org.mmfmilku.atom.agent.transport.handle.ServerHandle;
import org.mmfmilku.atom.agent.util.AgentLogUtils;
import org.mmfmilku.atom.agent.util.IOUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

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

    private ServerHandle<String> handle;

    public FServer(String listenPath) {
        this.listenPath = listenPath;
        handle = new BaseServerHandle();
    }

    public FServer(String listenPath, ServerHandle<String> handle) {
        this.listenPath = listenPath;
        this.handle = handle;
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
        listen(listen);
    }

    private void listen(File listen) {
        while (true) {
            // TODO @chenxp 2024/9/29 每行的数据完整读取
            File[] requestFiles = listen.listFiles((dir, name) -> !accepted.contains(name)
                    && name.endsWith(REQUEST));
            if (requestFiles != null) {
                for (File requestFile : requestFiles) {
                    accept(requestFile);
                }
            }
            sleep(5000);
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
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(requestFile);
            outputStream = new FileOutputStream(
                    new File(requestFile.getAbsolutePath() + RESPONSE));
            accepted.add(requestFile.getName());
            MsgContext ctx = new MsgContext(inputStream, outputStream,
                    // TODO 删除文件
//                    o -> accepted.remove(requestFile.getName())
                    o -> {
                        System.out.println("关闭" + o);
                    }
            );
            dealHandle(ctx);
        } catch (IOException e) {
            AgentLogUtils.error(e, "连接接收异常", requestFile.getAbsolutePath());
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
    }

    private ExecutorService executorService = new ThreadPoolExecutor(2, 2,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>() {
                @Override
                public boolean add(Runnable runnable) {
                    throw new RuntimeException("连接达到上限！");
                }

                @Override
                public boolean offer(Runnable runnable) {
                    throw new RuntimeException("连接达到上限！");
                }
            });

    private void dealHandle(MsgContext ctx) throws IOException {
        executorService.execute(() -> {
            boolean open = handle.open(ctx);
            if (!open) {
                throw new RuntimeException("连接建立失败");
            }
            while (!ctx.isClose()) {
                String read = ctx.read();
                if (read != null) {
                    handle.receive(ctx, read);
                }
            }
            System.out.println("执行结束" + ctx);
        });
    }

}


