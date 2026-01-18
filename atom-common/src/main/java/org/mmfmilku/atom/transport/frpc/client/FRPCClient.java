package org.mmfmilku.atom.transport.frpc.client;

import org.mmfmilku.atom.transport.protocol.client.ClientSession;
import org.mmfmilku.atom.transport.frpc.server.FRPCParam;
import org.mmfmilku.atom.transport.frpc.server.FRPCReturn;
import org.mmfmilku.atom.transport.protocol.client.FClient;
import org.mmfmilku.atom.transport.protocol.FClients;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;
import org.mmfmilku.atom.util.IOUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class FRPCClient {

    private int maxConnect = 10;
    private FClient fClient;
    private final List<FRPCSession> sessionList = new CopyOnWriteArrayList<>();
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
        fClient.setCloseCallback(connector -> {

        });
    }

    public FRPCReturn call(FRPCParam frpcParam) {
        for (int i = 0; i < sessionList.size(); i++) {
            FRPCSession frpcSession = sessionList.get(i);
            if (frpcSession.idle() && frpcSession.lock()) {
                try {
                    return frpcSession.call(frpcParam);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 关闭连接，移除连接列表
                    frpcSession.close();
                    // 并发情况下，实际移除时的下标可能非预期的对象，需要通过对象移除
                    sessionList.remove(frpcSession);
                    throw e;
                }
            }
        }
        if (sessionList.size() < maxConnect) {
            // double check 同步，保证不超上限
            synchronized (sessionList) {
                if (sessionList.size() < maxConnect) {
                    FRPCSession frpcSession = null;
                    FRPCReturn result;
                    try {
                        frpcSession = new FRPCSession(FClients.openAssemblySession(fClient));
                        // 局部变量，无需上锁
                        result = frpcSession.call(frpcParam);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (frpcSession != null) {
                            frpcSession.close();
                        }
                        throw e;
                    }
                    // 调用完后再添加至连接列表，避免被其他线程抢夺
                    sessionList.add(frpcSession);
                    return result;
                }
            }
            return lockSession().call(frpcParam);
        } else {
            return lockSession().call(frpcParam);
        }
    }

    private FRPCSession lockSession() {
        while (true) {
            // TODO 使用等待唤起
            for (FRPCSession frpcSession : sessionList) {
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
        sessionList.forEach(FRPCSession::close);
        sessionList.clear();
    }

    static class FRPCSession implements ClientSession<FRPCParam> {

        private ClientSession<TypeFrame> clientSession;

        private AtomicBoolean lock = new AtomicBoolean(false);

        FRPCSession(ClientSession<TypeFrame> clientSession) {
            this.clientSession = clientSession;
        }

        boolean idle() {
            return !lock.get();
        }

        boolean lock() {
            return lock.compareAndSet(false, true);
        }

        @Override
        public void send(FRPCParam data) {
            throw new RuntimeException("not support");
        }

        @Override
        public FRPCParam read() {
            throw new RuntimeException("not support");
        }

        @Override
        public FRPCParam sendThenRead(FRPCParam frpcParam) {
            byte[] serialize = IOUtils.serialize(frpcParam);
            TypeFrame typeFrame = clientSession.sendThenRead(new TypeFrame(serialize));
            frpcParam.setFrpcReturn(IOUtils.deserialize(typeFrame.getData()));
            return frpcParam;
        }

        public FRPCReturn call(FRPCParam frpcParam) {
            try {
                return sendThenRead(frpcParam).getFrpcReturn();
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
