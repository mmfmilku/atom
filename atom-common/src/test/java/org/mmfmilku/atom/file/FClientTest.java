package org.mmfmilku.atom.file;

import com.alibaba.fastjson.JSON;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.client.ClientSession;
import org.mmfmilku.atom.transport.protocol.client.FClient;
import org.mmfmilku.atom.transport.protocol.exception.ConnectException;
import org.mmfmilku.atom.transport.protocol.handle.ChannelContext;
import org.mmfmilku.atom.transport.protocol.handle.RRModeHandle;
import org.mmfmilku.atom.transport.protocol.handle.string.StringHandle;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * FClientTest
 *
 * @author chenxp
 * @date 2024/9/30:13:58
 */
public class FClientTest {

    private static int MAX_CONNECT = 20;
    private String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @BeforeClass
    public static void beforeClass() {
        FServerUtil.runServer(MAX_CONNECT,
                new TypeHandler(), new StringHandle(),
                (RRModeHandle<String>) (data, channelContext) ->
                        channelContext.write( "fserver接收到消息:" + data));
    }

    @Test
    public void testAll() throws InterruptedException, ExecutionException {
        // 综合测试
        // 1.测试连接与关闭连接，获取最大连接数
        List<ClientSession<String>> sessions = printCostTime((a) -> {
            try {
                return testConnectClose();
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
            return null;
        }, "testConnectClose");
        // 2.测试发送接收消息
        printCostTime((a) -> {
            try {
                testSendMessage(sessions);
                return null;
            } catch (InterruptedException | ExecutionException e) {
                fail(e.getMessage());
            }
            return null;
        }, "testSendMessage");
        // 3.测试复杂字符串的发送接收
        testRRModeClient(sessions);
        // 4.关闭所有连接
        for (ClientSession<String> connect : sessions) {
            connect.close();
        }
    }

    public List<ClientSession<String>> testConnectClose() throws InterruptedException {
        System.out.println("------------------第一次连接至上限--------------");
        List<ClientSession<String>> connects1 = connectToMax();
        assertEquals(connects1.size(), MAX_CONNECT);
        System.out.println("------------------第二次连一定数量--------------");
        List<ClientSession<String>> connects2 = connect(5);
        assertEquals(connects2.size(), 0);
        System.out.println("------------------关闭所有连接--------------");
        for (ClientSession<String> session : connects1) {
            session.close();
        }
        // 等待服务器完全释放连接
        Thread.sleep(2500);
        System.out.println("------------------第三次连接至上限--------------");
        List<ClientSession<String>> connects3 = connectToMax();
        assertEquals(connects3.size(), MAX_CONNECT);
        return connects3;
    }

    public void testSendMessage(List<ClientSession<String>> connects) throws InterruptedException, ExecutionException {
        int max = MAX_CONNECT;
        List<Future<?>> sendFutures = new ArrayList<>(max);
        for (int i = 0; i < connects.size(); i++) {
            int idx = i;
            Future<?> submit = executorService.submit(() -> {
                ClientSession<String> connect = connects.get(idx);
                String s = connect.sendThenRead("连接" + idx + "发送消息a");
                System.out.println("连接" + idx + "接收：" + s);
                assertEquals("fserver接收到消息:连接" + idx + "发送消息a", s);

                s = connect.sendThenRead("连接" + idx + "发送消息b");
                System.out.println("连接" + idx + "接收：" + s);
                assertEquals("fserver接收到消息:连接" + idx + "发送消息b", s);
            });
            sendFutures.add(submit);
        }
        for (Future<?> sendFuture : sendFutures) {
            sendFuture.get();
        }
    }

    private List<ClientSession<String>> connect(int connectNum) throws InterruptedException {
        FClient fClient = new FClient(path);
        int max = connectNum;
        List<ClientSession<String>> connects = new ArrayList<>(max);
        List<Future<ClientSession<String>>> futures = new ArrayList<>(max);
        long start = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
            Future<ClientSession<String>> submit =
                    executorService.submit((Callable<ClientSession<String>>) fClient::connect);
            futures.add(submit);
        }
        for (int i = 0; i < max; i++) {
            try {
                ClientSession<String> session = futures.get(i).get();
                connects.add(session);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("尝试连接：" + max + "," +
                "连接成功：" + connects.size() + "," +
                "耗时" + (end - start) + "ms");
        return connects;
    }

    private List<ClientSession<String>> connectToMax() throws InterruptedException {
        return connect(MAX_CONNECT);
    }

    public void testRRModeClient(List<ClientSession<String>> connects) {
        ClientSession<String> connect = connects.get(0);
        Map<String, Object> map = new HashMap<>();
        map.put("k1", "5");
        map.put("k2", "8");
        String s = connect.sendThenRead(JSON.toJSONString(map));
        assertEquals("fserver接收到消息:" + JSON.toJSONString(map), s);
    }

    private <T> T printCostTime(Function<String, T> function, String desc) {
        long start = System.currentTimeMillis();
        T apply = function.apply(null);
        long finish = System.currentTimeMillis();
        System.out.println(desc + "耗时：" + (finish - start));
        return apply;
    }
}