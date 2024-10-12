package org.mmfmilku.atom.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class CodeUtilsTest {

    @Test
    public void toClassName() {
        String s1 = CodeUtils.toClassName("aa/bb/cc/F1.java");
        System.out.println(s1);
        String s2 = CodeUtils.toClassName("aa/bb/cc/mm/TT.class");
        System.out.println(s2);
    }
}