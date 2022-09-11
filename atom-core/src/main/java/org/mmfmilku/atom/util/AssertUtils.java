package org.mmfmilku.atom.util;

public class AssertUtils {

    public static void notNull(Object o) {
        if (o == null)
            throw new NullPointerException();
    }

    public static void assertTrue(Boolean express) {
        assert Boolean.TRUE.equals(express);
    }

}
