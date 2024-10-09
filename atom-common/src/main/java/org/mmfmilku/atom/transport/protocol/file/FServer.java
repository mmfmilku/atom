package org.mmfmilku.atom.transport.protocol.file;

import org.mmfmilku.atom.transport.ConnectContext;
import org.mmfmilku.atom.transport.handle.file.FHandle;
import org.mmfmilku.atom.transport.handle.ServerHandle;
import org.mmfmilku.atom.util.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

    private static final String LISTEN_FILE = "fserver.listen";

    private String listenPath;

    private Map<String, ConnectContext> ctxMap = new HashMap<>();

    private ServerHandle<String> handle;

    private List<ServerHandle> handles = new ArrayList<>();

    private OutputStream outputStream;

    public FServer(String listenPath) {
        this.listenPath = listenPath;
        handle = new FHandle();
    }

    public FServer(String listenPath, ServerHandle<String> handle) {
        this.listenPath = listenPath;
        this.handle = handle;
    }

    public FServer addHandle(ServerHandle<String> handle) {
        handles.add(handle);
        return this;
    }

    public void start() {
        File listen = new File(listenPath);
        if (listen.isFile()) {
            throw new RuntimeException("启动失败,监听路径需为文件夹");
        }
        File[] files = listen.listFiles(filter -> filter.getName().equals(LISTEN_FILE));
        if (files != null && files.length != 0) {
            boolean delete = files[0].delete();
            if (!delete) {
                throw new RuntimeException("启动失败,监听文件夹已被占用");
            }
        }
        // TODO 对于历史文件的处理，先暴力删除
        try {
            Files.walk(Paths.get(listenPath))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            if (!listen.mkdirs()) {
                throw new RuntimeException("启动失败,创建目录失败");
            }
            listen(listen);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void listen(File listen) throws IOException {
        Path occupyPath = new File(listen, LISTEN_FILE).toPath();
        outputStream = new FileOutputStream(occupyPath.toFile());

        while (true) {
            File[] requestFiles = listen.listFiles((dir, name) -> !ctxMap.containsKey(name)
                    && name.endsWith(REQUEST));
            if (requestFiles != null) {
                for (File requestFile : requestFiles) {
                    accept(requestFile);
                }
            }
            sleep(3000);
        }
    }

    public void stop() {
        executorService.shutdown();
        ctxMap.forEach((name, ctx) -> {
            ctx.close();
        });
        IOUtils.closeStream(outputStream);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void accept(File requestFile) {
        // 拒绝的连接认为已处理，否则将不断处理
        ctxMap.put(requestFile.getName(), null);
        executorService.execute(() -> {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(requestFile);
                outputStream = new FileOutputStream(
                        new File(requestFile.getAbsolutePath() + RESPONSE));
                ConnectContext ctx = new ConnectContext(inputStream, outputStream,
                        // TODO 删除文件
                        o -> {
                            // ctxMap.remove(requestFile.getName());
                            System.out.println("关闭" + o);
                        }
                );
                boolean open = handle.onOpen(ctx);
                if (!open) {
                    throw new RuntimeException("连接建立失败");
                }
                handles.forEach(h -> h.onOpen(ctx));
                ctxMap.put(requestFile.getName(), ctx);
                while (!ctx.isClose()) {
                    if (ctx.canRead()) {
                        String read = ctx.read();
                        if (read != null) {
                            handle.onReceive(ctx, read);
                            // TODO
                            handles.forEach(h -> h.onReceive(ctx, read));
                        }
                    }
                }
                System.out.println("执行结束" + ctx);
            } catch (IOException e) {
                IOUtils.closeStream(inputStream);
                IOUtils.closeStream(outputStream);
            }

        });
    }

    private ExecutorService executorService = new ThreadPoolExecutor(2, 2,
            0L, TimeUnit.MILLISECONDS,
            // 队列大于1将入队失败
            new ArrayBlockingQueue<>(1),
            Executors.defaultThreadFactory(),
            (r, executor) -> System.out.println("连接" +
                    "拒绝")
    );


}


