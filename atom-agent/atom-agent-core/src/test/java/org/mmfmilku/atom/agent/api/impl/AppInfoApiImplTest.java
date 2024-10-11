package org.mmfmilku.atom.agent.api.impl;

import org.junit.Test;
import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.transport.frpc.FRPCStarter;
import org.mmfmilku.atom.transport.frpc.client.FRPCFactory;

import java.io.File;

import static org.junit.Assert.*;

public class AppInfoApiImplTest {

    @Test
    public void ping() {
        String fDir = System.getProperty("user.dir") + File.separator + "src/main/resources/test/fserver";

        // 单元测试使用的class资源与程序资源路径不同，服务扫描不到
        FRPCStarter starter = new FRPCStarter("org.mmfmilku.atom", fDir);
        starter.runServer();

        AppInfoApi remote = FRPCFactory.getService(AppInfoApi.class, fDir);
        AppInfoApi local = new AppInfoApiImpl();
        assertEquals(local.ping(), remote.ping());
    }
}