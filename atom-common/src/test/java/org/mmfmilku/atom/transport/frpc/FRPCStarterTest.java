package org.mmfmilku.atom.transport.frpc;

import org.junit.Test;
import org.mmfmilku.atom.util.IOUtils;
import org.mmfmilku.atom.util.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FRPCStarterTest {

    @Test
    public void testScanService() {
        try {
            FRPCStarter starter = new FRPCStarter("org.mmfmilku.atom");
            ReflectUtils.invokeMethod(starter, "scanService");
            List<Class<?>> classes = (List<Class<?>>)
                    ReflectUtils.getMember(starter, "classes");
            String[] expect = new String[]{"org.mmfmilku.atom.transport.frpc.api.FRpcService1",
                    "org.mmfmilku.atom.transport.frpc.api.FRpcService2"};
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
            FRPCStarter starter = new FRPCStarter("org.mmfmilku.atom");
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
            FRPCStarter starter = new FRPCStarter("org.mmfmilku.atom");
            ReflectUtils.invokeMethod(starter, "scanService");
            ReflectUtils.invokeMethod(starter, "mapService");
            Map<String, ServiceMapping> mappings = (Map<String, ServiceMapping>)
                    ReflectUtils.getMember(starter, "mappings");
            ServiceMapping fRpcService1 = mappings.get(
                    "org.mmfmilku.atom.transport.frpc.api.FRpcService1");
            ServiceMapping fRpcService2 = mappings.get(
                    "org.mmfmilku.atom.transport.frpc.api.FRpcService2");
            Object s1r1 = IOUtils.deserialize((byte[]) fRpcService1.execute("getList", null));
            System.out.println(s1r1);
            Object s1r2 = IOUtils.deserialize((byte[]) fRpcService1.execute("getList2", IOUtils.serialize(new String[] {
                    "c", "d"
            })));
            System.out.println(s1r2);
            Object s2r1 = IOUtils.deserialize((byte[]) fRpcService2.execute("getMap", IOUtils.serialize(new String[]{"3"})));
            System.out.println(s2r1);
            assertEquals("[{a=1}, {b=2}]", s1r1.toString());
            assertEquals("[{c=1}, {d=2}]", s1r2.toString());
            assertEquals("{k0=v0, k3=v3, k4=v4, k5=v5}", s2r1.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}