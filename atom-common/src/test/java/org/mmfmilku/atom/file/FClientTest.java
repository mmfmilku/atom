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
    public void connect() {
        FClient fClient = new FClient(path);
        ClientSession<String> connect1 = fClient.connect("1");
        ClientSession<String> connect2 = fClient.connect("2");

        String s = connect1.sendThenRead("发送消息1");
        System.out.println("接收：" + s);
        assertEquals("fserver接收到消息:发送消息1", s);

        s = connect2.sendThenRead("连接2发送消息1");
        System.out.println("接收：" + s);
        assertEquals("fserver接收到消息:连接2发送消息1", s);

        s = connect1.sendThenRead("发送消息2");
        System.out.println("接收：" + s);
        assertEquals("fserver接收到消息:发送消息2", s);

        connect1.close();

        s = connect2.sendThenRead("连接2发送消息2");
        System.out.println("接收：" + s);
        assertEquals("fserver接收到消息:连接2发送消息2", s);

        for (int j = 1; j <= 10; j++) {
            System.out.println("第" + j + "次发送");
            String read = printCostTime(i -> connect2.sendThenRead("loop msg"));
            System.out.println("第" + j + "次发送完成,接收:" + read);
            assertEquals("fserver接收到消息:loop msg", read);
        }

        connect2.close();

    }

    @Test
    public void testMaxConnect() throws InterruptedException, ExecutionException {
        int max = MAX_CONNECT;
        List<ClientSession<String>> connects = connectToMax();

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

        for (ClientSession<String> connect : connects) {
            connect.close();
        }

    }

    @Test
    public void testClose() throws InterruptedException {
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
        System.out.println("------------------第三次连接至上限--------------");
        List<ClientSession<String>> connects3 = connectToMax();
        assertEquals(connects3.size(), MAX_CONNECT);
        for (ClientSession<String> session : connects3) {
            session.close();
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

    @Test
    public void testCaseThree() {
        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
        FClient fClient = new FClient(path);
        ClientSession<String> connect = fClient.connect();
        String read = connect.sendThenRead("fuck");
        System.out.println(read);
        assertEquals("fserver接收到消息:fuck", read);
        connect.close();
    }

    @Test
    public void testRRModeClient() {
        ClientSession<String> connect = FServerUtil.connect();
        Map<String, Object> map = new HashMap<>();
        map.put("k1", "5");
        map.put("k2", "8");
        String s = connect.sendThenRead(JSON.toJSONString(map));
        assertEquals("fserver接收到消息:" + JSON.toJSONString(map), s);
        System.out.println("客户端接收" + s);
        connect.close();
    }

    private String printCostTime(Function<String, String> function) {
        long start = System.currentTimeMillis();
        String apply = function.apply(null);
        long finish = System.currentTimeMillis();
        System.out.println("耗时：" + (finish - start));
        return apply;
    }
}