package org.mmfmilku.atom.transport.frpc.client;

import org.junit.Test;
import org.mmfmilku.atom.transport.frpc.server.FRPCStarter;
import org.mmfmilku.atom.transport.frpc.api.FRpcServiceOne;
import org.mmfmilku.atom.transport.frpc.api.FRpcServiceTwo;
import org.mmfmilku.atom.transport.frpc.api.impl.FRpcServiceOneImpl;
import org.mmfmilku.atom.transport.frpc.api.impl.FRpcServiceTwoImpl;

import java.io.File;

import static org.junit.Assert.*;

/**
 * FRPCClientTest
 *
 * @author
 * @date 2024/10/8:10:52
 */
public class FRPCClientTest {

    String fDir = System.getProperty("user.dir") + File.separator + "test" + File.separator + "fserver1";

    @Test
    public void getService() {
        FRPCStarter starter = new FRPCStarter("org.mmfmilku.atom", fDir);
        starter.runServer();

        FRpcServiceOne remote = FRPCFactory.getService(FRpcServiceOne.class, fDir);
        FRpcServiceOne local = new FRpcServiceOneImpl();

        assertEquals(local.getList(), remote.getList());
        assertEquals(local.getList2("a", "d"), remote.getList2("a", "d"));
        assertEquals(local.getList2("a1", "d"), remote.getList2("a1", "d"));
        assertEquals(local.getList2("b", "d"), remote.getList2("b", "d"));
        assertEquals(local.getList2("a", "c"), remote.getList2("a", "c"));

        FRpcServiceTwo remote2 = FRPCFactory.getService(FRpcServiceTwo.class, fDir);
        FRpcServiceTwo local2 = new FRpcServiceTwoImpl();

        assertEquals(local2.getMap("1"), remote2.getMap("1"));
        assertEquals(local2.getMap("5"), remote2.getMap("5"));
        assertEquals(local2.getMap("3"), remote2.getMap("3"));


    }
}