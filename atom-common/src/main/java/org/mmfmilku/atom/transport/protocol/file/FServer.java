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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Server
 *
 * @author mmfmilku
 * @date 2024/9/29:16:49
 */
public class FServer {

    public static final String REQUEST = ".request";

    public static final String RESPONSE = ".response";

    private static final String LISTEN_FILE = "fserver.listen";

    /**
     * 用于标记已经处理过的连接
     * */
    private static final ConnectContext ACCEPTED_CTX = new ConnectContext(
            null, null, null);

    /**
     * 用于标记连接失败的连接
     * */
    private static final ConnectContext FAILED_CTX = new ConnectContext(
            null, null, null);

    private String listenPath;

    private Map<String, ConnectContext> ctxMap = new ConcurrentHashMap<>();

    private ServerHandle<String> handle;

    private List<ServerHandle> handles = new ArrayList<>();

    private OutputStream listenStream;

    private ScheduledExecutorService bossExecutor = Executors.newScheduledThreadPool(3,
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
                            "pool-fserver-boss-thread-" + threadNumber.getAndIncrement(),
                            0);
                    t.setDaemon(true);
                    if (t.getPriority() != Thread.NORM_PRIORITY)
                        t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                }
            });

    private ExecutorService workerExecutor = new ThreadPoolExecutor(2, 2,
            0L, TimeUnit.MILLISECONDS,
            // 队列长度等于最大连接数
            new ArrayBlockingQueue<>(10),
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
                            "pool-fserver-worker-thread-" + threadNumber.getAndIncrement(),
                            0);
                    t.setDaemon(true);
                    if (t.getPriority() != Thread.NORM_PRIORITY)
                        t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                }
            },
            (r, executor) -> System.out.println("连接拒绝")
    );

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

    public boolean isRunning() {
        return listenStream != null;
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
                throw new RuntimeException("fserver start fail,listen path is used");
            }
        }
        // TODO 对于历史文件的处理，先暴力删除
        try {
            if (Files.exists(Paths.get(listenPath))) {
                Files.walk(Paths.get(listenPath))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            listen.mkdirs();
            // 监听连接
            listener(listen);
            // 处理消息
            worker();
            // 清理连接
            cleaner();
        } catch (IOException e) {
            e.printStackTrace();
            this.stop();
            throw new RuntimeException("启动失败");
        }
    }

    private void listener(File listen) throws IOException {
        Path occupyPath = new File(listen, LISTEN_FILE).toPath();
        listenStream = new FileOutputStream(occupyPath.toFile());

        ScheduledFuture<?> scheduledFuture = bossExecutor.scheduleAtFixedRate(() -> {
            File[] requestFiles = listen.listFiles((dir, name) -> !ctxMap.containsKey(name)
                    && name.endsWith(REQUEST));
            if (requestFiles != null) {
                for (File requestFile : requestFiles) {
                    accept(requestFile);
                }
            }
            // 500ms执行一次
        }, 0, 300, TimeUnit.MILLISECONDS);

    }

    private void worker() {
        bossExecutor.scheduleWithFixedDelay(() -> {
            try {
                List<Callable<Boolean>> tasks = ctxMap.values()
                        .stream()
                        .filter(ctx ->  ctx != ACCEPTED_CTX && !ctx.isClose())
                        .map(ctx -> (Callable<Boolean>) () -> {
                            if (ctx.canRead()) {
                                String read = ctx.read();
                                if (read != null) {
                                    handle.onReceive(ctx, read);
                                    // TODO
                                    handles.forEach(h -> h.onReceive(ctx, read));
                                }
                                return true;
                            }
                            return false;
                        }).collect(Collectors.toList());
//                System.out.println("tasks.size:" + tasks.size());
                // 将等待所有任务执行完成，否则会不断发起定时任务，导致重复线程处理同一条连接
                List<Future<Boolean>> futures = workerExecutor.invokeAll(tasks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void cleaner() {
        bossExecutor.scheduleAtFixedRate(() -> {
            try {
                Iterator<Map.Entry<String, ConnectContext>> itr = ctxMap.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<String, ConnectContext> next = itr.next();
                    // 获取关闭的上下文
                    ConnectContext ctx = next.getValue();
                    if (ctx != ACCEPTED_CTX && ctx.isClose()) {
                        // 删除关闭连接的文件
                        String fileName = next.getKey();
                        if (new File(fileName).delete()
                                && new File(fileName + RESPONSE).delete()) {
                            // 移除引用
                            itr.remove();
                        } else {
                            // 文件删除失败，再次发起关闭，确保流被关闭
//                            System.out.println("ctx clear fail,file can not delete:" + fileName);
//                            ctx.close();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void stop() {
        bossExecutor.shutdown();
        workerExecutor.shutdown();
        ctxMap.forEach((name, ctx) -> ctx.close());
        IOUtils.closeStream(listenStream);
        listenStream = null;
    }

    public void accept(File requestFile) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // 拒绝的连接认为已处理，否则将不断处理
            ctxMap.put(requestFile.getName(), ACCEPTED_CTX);
            inputStream = new FileInputStream(requestFile);
            outputStream = new FileOutputStream(
                    new File(requestFile.getAbsolutePath() + RESPONSE));
            ConnectContext ctx = new ConnectContext(inputStream, outputStream,
                    o -> System.out.println("关闭" + o)
            );
            boolean open = handle.onOpen(ctx);
            if (!open) {
                throw new RuntimeException("连接建立失败");
            }
            ctxMap.put(requestFile.getName(), ctx);
            handles.forEach(h -> h.onOpen(ctx));
        } catch (Exception e) {
            // 建立连接失败，关闭流
            e.printStackTrace();
            ctxMap.put(requestFile.getName(), FAILED_CTX);
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
    }

}


