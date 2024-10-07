package org.mmfmilku.atom.transport.frpc;

import org.junit.Test;
import org.mmfmilku.atom.util.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
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

}