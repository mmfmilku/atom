package org.mmfmilku.atom.util;

public class AssertUtils {

    public static void notNull(Object o) {
        if (o == null)
            throw new NullPointerException();
    }

}
