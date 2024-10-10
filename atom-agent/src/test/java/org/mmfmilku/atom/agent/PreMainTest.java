package org.mmfmilku.atom.agent;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PreMainTest {

    private int getNum() {
        return 2;
    }

    private int getSum(long a, long b) {
        return BigDecimal.valueOf(a).add(new BigDecimal(b)).intValue();
    }

    /**
     * 启动参数添加 -javaagent:F:\dev\project\atom\atom-agent\atom-agent-core\target\atom-agent-core-jar-with-dependencies.jar=base-path=F:\dev\project\atom\atom-agent\src\main\resources\test;k2=v2
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
