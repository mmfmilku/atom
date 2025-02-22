package org.mmfmilku.atom.transport.frpc;

import org.junit.Test;
import org.mmfmilku.atom.transport.frpc.api.FRpcServiceOne;
import org.mmfmilku.atom.transport.frpc.api.FRpcServiceTwo;
import org.mmfmilku.atom.transport.frpc.api.impl.FRpcServiceOneImpl;
import org.mmfmilku.atom.transport.frpc.api.impl.FRpcServiceTwoImpl;
import org.mmfmilku.atom.transport.frpc.client.FRPCClient;
import org.mmfmilku.atom.transport.frpc.server.FRPCParam;
import org.mmfmilku.atom.transport.frpc.server.FRPCStarter;
import org.mmfmilku.atom.transport.frpc.server.ServiceMapping;
import org.mmfmilku.atom.util.ReflectUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FRPCStarterTest {

    String fDir = System.getProperty("user.dir") + File.separator + "test" + File.separator + "fserver2";

    @Test
    public void testScanService() {
        try {
            FRPCStarter starter = new FRPCStarter("org.mmfmilku.atom", fDir);
            ReflectUtils.invokeMethod(starter, "scanService");
            List<Class<?>> classes = (List<Class<?>>)
                    ReflectUtils.getMember(starter, "classes");
            String[] expect = new String[]{FRpcServiceOneImpl.class.getName(),
                    FRpcServiceTwoImpl.class.getName()};
            Object[] actual = classes.stream().map(Class::getName).toArray();
            assertArrayEquals(expect, actual);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testMapService() {
        try {
            FRPCStarter starter = new FRPCStarter("org.mmfmilku.atom", fDir);
            ReflectUtils.invokeMethod(starter, "scanService");
            ReflectUtils.invokeMethod(starter, "mapService");
            Map<String, ServiceMapping> mappings = (Map<String, ServiceMapping>)
                    ReflectUtils.getMember(starter, "mappings");
            System.out.println(mappings);
            assertEquals(2, mappings.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testMappingExecute() {
        try {
            FRPCStarter starter = new FRPCStarter("org.mmfmilku.atom", fDir);
            ReflectUtils.invokeMethod(starter, "scanService");
            ReflectUtils.invokeMethod(starter, "mapService");
            Map<String, ServiceMapping> mappings = (Map<String, ServiceMapping>)
                    ReflectUtils.getMember(starter, "mappings");

            FRPCParam frpcParam = new FRPCParam();
            frpcParam.setServiceClass(FRpcServiceOne.class.getName());
            frpcParam.setApiName("getList");
            Object s1r1 = mappings.get(frpcParam.getServiceClass())
                    .execute(frpcParam.getApiName(), frpcParam).getData();
            System.out.println(s1r1);

            frpcParam.setServiceClass(FRpcServiceOne.class.getName());
            frpcParam.setApiName("getList2");
            frpcParam.setData(new Object[]{"c", "d"});
            Object s1r2 = mappings.get(frpcParam.getServiceClass())
                    .execute(frpcParam.getApiName(), frpcParam).getData();
            System.out.println(s1r2);

            frpcParam.setServiceClass(FRpcServiceTwo.class.getName());
            frpcParam.setApiName("getMap");
            frpcParam.setData(new Object[]{"3"});
            Object s2r1 = mappings.get(frpcParam.getServiceClass())
                    .execute(frpcParam.getApiName(), frpcParam).getData();
            System.out.println(s2r1);

            assertEquals("[{a=1}, {b=2}]", s1r1.toString());
            assertEquals("[{c=1}, {d=2}]", s1r2.toString());
            assertEquals("{k0=v0, k3=v3, k4=v4, k5=v5}", s2r1.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testRunServer() {
        FRPCStarter starter = new FRPCStarter("org.mmfmilku.atom", fDir);
        starter.runServer();

        FRPCClient frpcClient = FRPCClient.getInstance(fDir);

        FRPCParam frpcParam = new FRPCParam();
        frpcParam.setServiceClass(FRpcServiceOne.class.getName());
        frpcParam.setApiName("getList2");
        frpcParam.setData(new Object[]{"e", "f"});
        Object data = frpcClient.call(frpcParam).getData();
        assertEquals("[{e=1}, {f=2}]", data.toString());

        frpcParam.setServiceClass(FRpcServiceTwo.class.getName());
        frpcParam.setApiName("getMap");
        frpcParam.setData(new Object[]{"4"});
        data = frpcClient.call(frpcParam).getData();
        data = frpcClient.call(frpcParam).getData();
        data = frpcClient.call(frpcParam).getData();
        assertEquals("{k0=v0, k4=v4, k5=v5}", data.toString());
    }

}