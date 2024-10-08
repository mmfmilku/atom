package org.mmfmilku.atom.transport.frpc.client;

import org.mmfmilku.atom.transport.client.ClientSession;
import org.mmfmilku.atom.transport.frpc.FRPCParam;
import org.mmfmilku.atom.transport.frpc.FRPCReturn;
import org.mmfmilku.atom.transport.frpc.FRPCStarter;
import org.mmfmilku.atom.transport.protocol.file.FClient;
import org.mmfmilku.atom.util.IOUtils;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class FRPCClient {

    private int maxConnect = 10;
    private FClient fClient;
    private final List<FRPCSession> clientList = new CopyOnWriteArrayList<>();

    public FRPCClient() {
        fClient = new FClient(FRPCStarter.F_SERVER_DIR);
    }

    public FRPCReturn call(ClientSession<String> session, FRPCParam frpcParam) {
        byte[] serialize = IOUtils.serialize(frpcParam);
        String toSend = Base64.getEncoder().encodeToString(serialize);
        String read = session.sendThenRead(toSend + "\r");
        byte[] decode = Base64.getDecoder().decode(read.trim());
        return IOUtils.deserialize(decode);
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
                    FRPCReturn result = call(frpcSession, frpcParam);
                    clientList.add(frpcSession);
                    return result;
                }
            }
            return call(lockSession(), frpcParam);
        } else {
            return call(lockSession(), frpcParam);
        }
    }

    private FRPCSession lockSession () {
        while (true) {
            // TODO 使用等待唤起
            for (FRPCSession frpcSession : clientList) {
                if (frpcSession.lock()) {
                    return frpcSession;
                }
            }
        }
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
