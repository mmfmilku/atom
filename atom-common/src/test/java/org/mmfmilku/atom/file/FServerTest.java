//package org.mmfmilku.atom.file;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import org.junit.Test;
//import org.mmfmilku.atom.transport.ConnectContext;
//import org.mmfmilku.atom.transport.MessageUtils;
//import org.mmfmilku.atom.transport.handle.RRModeServerHandle;
//import org.mmfmilku.atom.transport.protocol.file.FServer;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//
//public class FServerTest {
//
//    @Test
//    public void testStart() {
//        String path = System.getProperty("user.dir") + "\\src\\main\\resources\\test\\transport";
//        FServer fServer = new FServer(path);
//        fServer.start();
//    }
//
//    @Test
//    public void rrModeServer() {
//        FServerUtil.runServer(new RRModeServerHandle() {
//            @Override
//            public void onReceive(ConnectContext ctx, String data) {
//                JSONObject jsonObject = JSON.parseObject(data);
//                String v1 = (String) jsonObject.get("k1");
//                String v2 = (String) jsonObject.get("k2");
//                int i = Integer.parseInt(v1) + Integer.parseInt(v2);
//                ctx.write( "服务器返回：" + i);
//            }
//        });
//    }
//
//    @Test
//    public void testLength() {
//        for (int i = 0; i <= 65535; i+=1) {
//            byte[] bytes = MessageUtils.codeLength(i);
//            int i1 = MessageUtils.decodeLength(bytes);
//            assertEquals(i, i1);
//        }
//        try {
//            MessageUtils.codeLength(65536);
//            fail("65536长度超长未校验");
//        } catch (Exception ignored) {
//
//        }
//    }
//
//}