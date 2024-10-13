package org.mmfmilku.atom.agent;

import org.junit.Before;
import org.junit.Test;
import org.mmfmilku.atom.api.AppInfoApi;
import org.mmfmilku.atom.api.InstrumentApi;
import org.mmfmilku.atom.transport.frpc.client.FRPCFactory;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class PreMainTest {

    private int getNum() {
        return 2;
    }

    private int getSum(long a, long b) {
        return BigDecimal.valueOf(a).add(new BigDecimal(b)).intValue();
    }

    @Before
    public void before() {
        String fDir = "F:\\dev\\project\\atom\\atom-agent\\src\\main\\resources\\test\\fserver";
        AppInfoApi infoApi = FRPCFactory.getService(AppInfoApi.class, fDir);
        InstrumentApi instrumentApi = FRPCFactory.getService(InstrumentApi.class, fDir);

        System.out.println(infoApi.ping());

        instrumentApi.retransformClass(this.getClass().getName());

        List<String> searchClass = instrumentApi.searchClassForPage(1, 10, "a");
        System.out.println(searchClass);
    }

    /**
     * 启动参数添加 -javaagent:F:\dev\project\atom\atom-web-console\src\main\resources\jar\atom-agent-core-0.0.1-SNAPSHOT-jar-with-dependencies.jar=base-path=F:\dev\project\atom\atom-agent\src\main\resources\test\ord;k2=v2;app-fserver-dir=F:\dev\project\atom\atom-agent\src\main\resources\test\fserver;app-base-package=org.mmfmilku.atom.agent.api.impl
     * */
    @Test
    public void testCase1() {
        int num = getNum();
        System.out.println("num=" + num);
        assertEquals(3, num);

        int sum = getSum(3, 9);
        System.out.println("sum=" + sum);
        assertEquals(sum, 27);
    }

}
