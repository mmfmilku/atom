package org.mmfmilku.atom.agent;

import org.junit.Test;

import java.math.BigDecimal;

public class PreMainTest {

    private int getNum() {
        System.out.println("ggggggg");
        if (true) {
            System.out.println("cccccc");
        }
        return 3;
    }

    private int getSum(int a, int b) {
        return BigDecimal.valueOf(a).multiply(new BigDecimal(b)).intValue();
    }

}
