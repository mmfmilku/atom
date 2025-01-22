package org.mmfmilku.atom.transport.protocol.base;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.handle.HandleManager;
import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;
import org.mmfmilku.atom.util.IOUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
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
    private static final Connector ACCEPTED_CTX = new Connector(
            null, null, null);

    /**
     * 用于标记连接失败的连接
     * */
    private static final Connector FAILED_CTX = new Connector(
            null, null, null);

    private String listenPath;

    private ConcurrentMap<String, Connector> ctxMap = new ConcurrentHashMap<>();

    private HandleManager handleManager;

    private OutputStream listenStream;

    private int boosThreadSize = 3;
    private int workerThreadSize = 3;
    // 最大客户端连接数
    private int maxConnect = 10;
    // 检测连接间隔毫秒时间
    private long connectDelay = 300;
    // 接收消息间隔毫秒时间
    private long receiveDelay = 100;
    // 清理断开连接的通信文件时间间隔，秒
    private long clearDelay = 2;

    // 读取完整帧超时时间，毫秒
    private long readTimeOutMillis = 2000;

    private ScheduledExecutorService bossExecutor;

    private ExecutorService workerExecutor;

    public FServer(String listenPath) {
        this.listenPath = listenPath;

        handleManager = new HandleManager();
        handleManager.setReadTimeOutMillis(readTimeOutMillis);
    }

    public FServer(String listenPath, int maxConnect) {
        this.listenPath = listenPath;

        this.maxConnect = maxConnect;

        handleManager = new HandleManager();
        handleManager.setReadTimeOutMillis(readTimeOutMillis);
    }

    public FServer addHandle(ServerHandle handle) {
        handleManager.addHandle(handle);
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
            // 初始化线程池
            init();
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

    private void init() {
        bossExecutor = Executors.newScheduledThreadPool(boosThreadSize,
                new DaemonThreadFactory("pool-fserver-boss-thread"));

        workerExecutor = new ThreadPoolExecutor(workerThreadSize, workerThreadSize,
                0L, TimeUnit.MILLISECONDS,
                // 工作线程除收集数据外还负责为新连接握手，队列长度保证余量
                new ArrayBlockingQueue<>(maxConnect  * 2),
                new DaemonThreadFactory("pool-fserver-worker-thread"),
                (r, executor) -> System.out.println("服务器连接已满")
        );
    }

    private void listener(File listen) throws IOException {
        Path occupyPath = new File(listen, LISTEN_FILE).toPath();
        listenStream = new FileOutputStream(occupyPath.toFile());
        String runtimeStr = ManagementFactory.getRuntimeMXBean().getName();
        listenStream.write(runtimeStr.getBytes());

        ScheduledFuture<?> scheduledFuture = bossExecutor.scheduleAtFixedRate(() -> {
            File[] requestFiles = listen.listFiles((dir, name) -> !ctxMap.containsKey(name)
                    && name.endsWith(REQUEST));
            if (requestFiles != null) {
                for (File requestFile : requestFiles) {
                    // 使用工作线程接收连接
                    workerExecutor.submit(() -> accept(requestFile));
                }
            }
            // 执行一次的时间
        }, 0, connectDelay, TimeUnit.MILLISECONDS);

    }

    private void worker() {
        bossExecutor.scheduleWithFixedDelay(() -> {
            try {
                List<Callable<Boolean>> tasks = ctxMap.values()
                        .stream()
                        .filter(ctx -> ctx != ACCEPTED_CTX && ctx != FAILED_CTX && !ctx.isClose())
                        .map(ctx -> (Callable<Boolean>) () -> {
                            FFrame read = ctx.tryRead();
                            if (read != null) {
                                handleManager.onReceive(ctx, read);
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
        }, 0, receiveDelay, TimeUnit.MILLISECONDS);
    }

    private void cleaner() {
        bossExecutor.scheduleAtFixedRate(() -> {
            try {
//                System.out.println("执行清理任务 " + ctxMap.size());
                Iterator<Map.Entry<String, Connector>> itr = ctxMap.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry<String, Connector> next = itr.next();
                    // 获取关闭的上下文
                    Connector ctx = next.getValue();
                    if (    (ctx != ACCEPTED_CTX && ctx.isClose()) || ctx == FAILED_CTX
                    ) {
                        // 删除关闭连接的文件
                        String fileName = next.getKey();
                        System.out.println("清理 " + fileName);
                        if (deleteFile(new File(listenPath, fileName))
                                && deleteFile(new File(listenPath, fileName + RESPONSE))) {
                            // 移除引用
                            itr.remove();
                        }
                    }
                }
//                System.out.println("清理任务结束 " + ctxMap.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, clearDelay, TimeUnit.SECONDS);
    }

    private boolean deleteFile(File file) {
        return !file.exists() || file.delete();
    }

    public void stop() {
        bossExecutor.shutdown();
        workerExecutor.shutdown();
        ctxMap.forEach((name, ctx) -> ctx.close());
        IOUtils.closeStream(listenStream);
        listenStream = null;
    }

    private void accept(File requestFile) {
        if (ctxMap.size() >= maxConnect) {
            Connector absent = ctxMap.putIfAbsent(requestFile.getName(), FAILED_CTX);
            if (absent == null) {
                try (OutputStream outputStream = new FileOutputStream(
                        new File(requestFile.getAbsolutePath() + RESPONSE))) {
                    outputStream.write(MessageUtils.codeLength(0));
                } catch (IOException ignored) {}
            }
            return;
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // 拒绝的连接认为已处理，否则将不断处理
            // 不存在的情况put
            Connector putted = ctxMap.putIfAbsent(requestFile.getName(), ACCEPTED_CTX);
            if (putted != null) {
                // 旧值不为空表示发生并发
                return;
            }
            inputStream = new FileInputStream(requestFile);
            outputStream = new FileOutputStream(
                    new File(requestFile.getAbsolutePath() + RESPONSE));
            Connector ctx = new Connector(inputStream, outputStream,
                    o -> System.out.println("关闭" + o)
            );
            boolean open = handleManager.openConnect(ctx);
            if (!open) {
                throw new RuntimeException("连接建立失败" + requestFile.getName());
            }
            ctxMap.put(requestFile.getName(), ctx);
        } catch (Exception e) {
            // 建立连接失败，关闭流
            e.printStackTrace();
            ctxMap.put(requestFile.getName(), FAILED_CTX);
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
    }

}


