package org.mmfmilku.atom.util;

import org.mmfmilku.atom.exception.SystemException;

import java.util.Collection;

public class AssertUtil {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new SystemException(message);
        }
    }

    public static void notnull(Object obj, String message) {
        if (obj == null) {
            throw new SystemException(message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new SystemException(message);
        }
    }

}
