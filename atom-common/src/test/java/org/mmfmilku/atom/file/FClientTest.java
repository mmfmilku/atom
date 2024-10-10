//package org.mmfmilku.atom.file;
//
//import com.alibaba.fastjson.JSON;
//import org.junit.Test;
//import org.mmfmilku.atom.transport.client.ClientSession;
//import org.mmfmilku.atom.transport.protocol.file.FClient;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
///**
// * FClientTest
// *
// * @author chenxp
// * @date 2024/9/30:13:58
// */
//public class FClientTest {
//
//    @Test
//    public void connect() {
//        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
//        FClient fClient = new FClient(path);
//        ClientSession<String> connect1 = fClient.connect("1");
//        ClientSession<String> connect2 = fClient.connect("2");
//
//        String s = connect1.sendThenRead("发送消息1");
//        System.out.println("接收：" + s);
//
//        s = connect2.sendThenRead("连接2发送消息1");
//        System.out.println("接收：" + s);
//
//        s = connect1.sendThenRead("发送消息2");
//        System.out.println("接收：" + s);
//
//        connect1.close();
//
//        s = connect2.sendThenRead("连接2发送消息2");
//        System.out.println("接收：" + s);
//
//        for (int j = 1; j <= 10; j++) {
//            System.out.println("第" + j + "次发送");
//            String read = printCostTime(i -> connect2.sendThenRead("loop msg"));
//            System.out.println("第" + j + "次发送完成,接收:" + read);
//        }
//
//        connect2.close();
//
//    }
//
//    @Test
//    public void testClose() {
//        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
//        FClient fClient = new FClient(path);
//        ClientSession<String> connect1 = fClient.connect("c1");
//        ClientSession<String> connect2 = fClient.connect("c2");
//        connect1.close();
//        connect2.close();
//
//        ClientSession<String> connect3 = fClient.connect("c3");
//        ClientSession<String> connect4 = fClient.connect("c4");
//
//        connect3.close();
//
//        ClientSession<String> connect5 = fClient.connect("c5");
//
//        connect4.close();
//        connect5.close();
//    }
//
//    @Test
//    public void testCaseThree() {
//        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
//        FClient fClient = new FClient(path);
//        ClientSession<String> connect = fClient.connect();
//        String read = connect.sendThenRead("fuck");
//        System.out.println(read);
//        connect.close();
//    }
//
//    @Test
//    public void testRRModeClient() {
//        ClientSession<String> connect = FServerUtil.connect();
//        Map<String, Object> map = new HashMap<>();
//        map.put("k1", "5");
//        map.put("k2", "8");
//        String s = connect.sendThenRead(JSON.toJSONString(map));
//        System.out.println("客户端接收" + s);
//    }
//
//    private String printCostTime(Function<String, String> function) {
//        long start = System.currentTimeMillis();
//        String apply = function.apply(null);
//        long finish = System.currentTimeMillis();
//        System.out.println("耗时：" + (finish - start));
//        return apply;
//    }
//}