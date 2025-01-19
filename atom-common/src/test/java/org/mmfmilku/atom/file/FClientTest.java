package org.mmfmilku.atom.file;

import com.alibaba.fastjson.JSON;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.client.ClientSession;
import org.mmfmilku.atom.transport.protocol.handle.RRModeServerHandle;
import org.mmfmilku.atom.transport.protocol.client.FClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * FClientTest
 *
 * @author chenxp
 * @date 2024/9/30:13:58
 */
public class FClientTest {

    @BeforeClass
    public static void beforeClass() {
        FServerUtil.runServer(new RRModeServerHandle() {
            @Override
            public void onReceive(Connector ctx, String data) {
                ctx.write( "fserver接收到消息:" + data);
            }
        });
    }

    @Test
    public void connect() {
        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
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
    public void testMaxConnect() {
        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
        FClient fClient = new FClient(path);
        int max = 10;
        List<ClientSession<String>> connects = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            ClientSession<String> connect = fClient.connect();
            connects.add(connect);
        }
        for (int i = 0; i < max; i++) {
            ClientSession<String> connect = connects.get(i);
            String s = connect.sendThenRead("连接" + i + "发送消息a");
            System.out.println("连接" + i + "接收：" + s);
            assertEquals("fserver接收到消息:连接" + i + "发送消息a", s);

            s = connect.sendThenRead("连接" + i + "发送消息b");
            System.out.println("连接" + i + "接收：" + s);
            assertEquals("fserver接收到消息:连接" + i + "发送消息b", s);
        }

        for (int i = 0; i < max; i++) {
            ClientSession<String> connect = connects.get(i);
            connect.close();
        }

    }

    @Test
    public void testClose() {
        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
        FClient fClient = new FClient(path);
        ClientSession<String> connect1 = fClient.connect("c1");
        ClientSession<String> connect2 = fClient.connect("c2");
        ClientSession<String> connect3 = fClient.connect("c3");
        ClientSession<String> connect4 = fClient.connect("c4");
        ClientSession<String> connect5 = fClient.connect("c5");


        connect5.send("5555");

        connect1.close();
        connect2.close();
        connect3.close();
        connect4.close();
        connect5.close();

        ClientSession<String> connect6 = fClient.connect("c6");
        connect6.close();

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