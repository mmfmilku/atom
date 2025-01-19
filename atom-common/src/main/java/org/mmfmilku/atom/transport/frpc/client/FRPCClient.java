package org.mmfmilku.atom.transport.frpc.client;

import org.mmfmilku.atom.transport.protocol.client.ClientSession;
import org.mmfmilku.atom.transport.frpc.server.FRPCParam;
import org.mmfmilku.atom.transport.frpc.server.FRPCReturn;
import org.mmfmilku.atom.transport.protocol.client.FClient;
import org.mmfmilku.atom.util.IOUtils;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class FRPCClient {

    private int maxConnect = 10;
    private FClient fClient;
    private final List<FRPCSession> clientList = new CopyOnWriteArrayList<>();
//    private Lock lock = new ReentrantLock(false);
//    private Condition waitCall = lock.newCondition();

    private final static Map<String, FRPCClient> frpcClientMap = new ConcurrentHashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            frpcClientMap.forEach((k, v) -> v.close());
        }));
    }

    public static FRPCClient getInstance(String fDir) {
        if (frpcClientMap.containsKey(fDir)) {
            return frpcClientMap.get(fDir);
        }
        // double check
        synchronized (frpcClientMap) {
            if (!frpcClientMap.containsKey(fDir)) {
                frpcClientMap.put(fDir, new FRPCClient(fDir));
            }
        }
        return frpcClientMap.get(fDir);
    }

    private FRPCClient(String fDir) {
        fClient = new FClient(fDir);
    }

    public FRPCReturn call(ClientSession<String> session, FRPCParam frpcParam) {
        try {
            byte[] serialize = IOUtils.serialize(frpcParam);
            String toSend = Base64.getEncoder().encodeToString(serialize);
            String read = session.sendThenRead(toSend + "\r");
            byte[] decode = Base64.getDecoder().decode(read.trim());
            return IOUtils.deserialize(decode);
        } finally {
//            waitCall.signalAll();
        }
    }

    public FRPCReturn call(FRPCParam frpcParam) {
        boolean idle = clientList.stream().anyMatch(FRPCSession::idle);
        if (idle) {
            for (FRPCSession frpcSession : clientList) {
                if (frpcSession.lock()) {
                    return call(frpcSession, frpcParam);
                }
            }
        }
        if (clientList.size() < maxConnect) {
            // double check 同步，保证不超上限
            synchronized (clientList) {
                if (clientList.size() < maxConnect) {
                    FRPCSession frpcSession = new FRPCSession(fClient.connect());
                    // 局部变量，无需上锁
                    FRPCReturn result = call(frpcSession, frpcParam);
                    // 调用完后再添加至连接列表，避免被其他线程抢夺
                    clientList.add(frpcSession);
                    return result;
                }
            }
            return call(lockSession(), frpcParam);
        } else {
            return call(lockSession(), frpcParam);
        }
    }

    private FRPCSession lockSession() {
        while (true) {
            // TODO 使用等待唤起
            for (FRPCSession frpcSession : clientList) {
                if (frpcSession.lock()) {
                    return frpcSession;
                }
            }
//            try {
//                waitCall.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
        }
    }

    public void close() {
        clientList.forEach(FRPCSession::close);
    }

    static class FRPCSession implements ClientSession<String> {

        private ClientSession<String> clientSession;

        private AtomicBoolean lock = new AtomicBoolean(false);

        FRPCSession(ClientSession<String> clientSession) {
            this.clientSession = clientSession;
        }

        boolean idle() {
            return !lock.get();
        }

        boolean lock() {
            return lock.compareAndSet(false, true);
        }

        @Override
        public void send(String data) {
            throw new RuntimeException("not support");
        }

        @Override
        public String read() {
            throw new RuntimeException("not support");
        }

        @Override
        public String sendThenRead(String data) {
            try {
                return clientSession.sendThenRead(data);
            } finally {
                lock.set(false);
            }
        }

        @Override
        public void close() {
            clientSession.close();
        }
    }

}
