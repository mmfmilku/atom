package org.mmfmilku.atom.agent.compiler.class_for_input_test;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件描述
 **/
public class InputForGetJavaAST {

    public void test1() {
        String a = "value of a";
        int b = 4;
        int c = 6;
        int d = b * c;
        System.out.println(d);
        System.out.println(a.length());
        a = "new value of a";
        d = c = b = a.length();

        Map m = new HashMap();
        m.put("k1", "v1");

        String mString = m.toString();
    }

    public void test2() {
        String a = "value of a";
        int b = 4;
        int c = 6;
        int d = b * c;
        System.out.println(d);
        System.out.println(a.length());
        a = "new value of a";
        d = c = b = a.length();

        Map m = new HashMap();
        m.put("k1", "v1");

        String mString = m.toString();
    }

}
