package org.mmfmilku.atom.transport.frpc.client;

import org.junit.Test;
import org.mmfmilku.atom.transport.frpc.api.FRpcServiceOne;
import org.mmfmilku.atom.transport.frpc.api.impl.FRpcServiceOneImpl;

/**
 * FRPCClientTest
 *
 * @author
 * @date 2024/10/8:10:52
 */
public class FRPCClientTest {

    @Test
    public void getService() {

        FRpcServiceOne service = FRPCFactory.getService(FRpcServiceOne.class);

        System.out.println(service.hashCode());

        System.out.println(service.toString());

        System.out.println(service.getList());
        System.out.println(service.getList2("a", "b"));

    }
}