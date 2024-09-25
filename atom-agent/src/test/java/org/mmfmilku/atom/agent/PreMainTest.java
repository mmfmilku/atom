package org.mmfmilku.atom.agent;

import org.junit.Test;
import static org.junit.Assert.*;

public class PreMainTest {

    private int getNum() {
        return 2;
    }

    /**
     * 启动参数添加 -javaagent:F:\dev\project\atom\atom-agent\atom-agent-core\target\atom-agent-core-jar-with-dependencies.jar=base-path=F:\dev\project\atom\atom-agent\src\main\resources\test;k2=v2
     * */
    @Test
    public void testCase1() {
        int num = getNum();
        assertEquals(3, num);
        System.out.println(getNum());
    }

}
