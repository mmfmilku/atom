package org.mmfmilku.atom.agent.compiler.parser.aa;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TestFile3 implements Serializable {

    public TestFile3() {

    }

    public void forCase1() {
        int b = 0;
        for (int i = 0; i < 10; i++) {
            System.out.println("for");
        }
        for (int i = 0; i < 10; b = 3) {
            i++;
            System.out.println("for");
        }
    }

//    public void forCase3() {
//        int b = 1;
//        while (b > 0) {
//            b = 0;
//            System.out.println("while");
//        }
//    }
//
//    public void forCase2() {
//        List<Integer> list = Arrays.asList(1, 2, 4);
//        for (Integer integer : list) {
//            System.out.println("enhance for");
//        }
//    }
//
//    public void forCase4() {
//        int b = 0;
//        do {
//            b = 1;
//            System.out.println("do while");
//        } while (b == 0);
//    }

}