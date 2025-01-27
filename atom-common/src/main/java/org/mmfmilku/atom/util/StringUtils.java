package org.mmfmilku.atom.util;

/**
 * StringUtils
 *
 * @author mmfmilku
 * @date 2024/8/2:15:33
 */
public class StringUtils {
    
    private static final String EMPTY_STR = "";
    
    /**
     * 获取某字符串右边的字符串
     * */
    public static String right(String str, String forRight) {
        if (isEmpty(str)) {
            return str;
        }
        int i = str.indexOf(forRight);
        if (i < 0) {
            return str;
        }
        return str.substring(i + forRight.length());
    }

    /**
     * 获取某字符串最右边的字符串
     * */
    public static String rightmost(String str, String forRight) {
        if (isEmpty(str)) {
            return str;
        }
        int i = str.lastIndexOf(forRight);
        if (i < 0) {
            return str;
        }
        return str.substring(i + forRight.length());
    }
    
    /**
     * 截取两个字符串中间的字符串
     * */
    public static String between(String str, String startStr, String endStr) {
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        int i = str.indexOf(startStr);
        int j = str.indexOf(endStr);
        if (i < 0) {
            if (j < 0) {
                return EMPTY_STR;
            }
            // no start
            return str.substring(0, j);
        }

        if (j < 0) {
            // no end
            return str.substring(i + startStr.length());
        }
        //  between start and end
        return str.substring(i + startStr.length(), j);
    }

    /**
     * 获取两个字符串包裹的内容
     * (a22ab33bf, a, b) -> 22ab33
     * */
    public static String surroundedStr(String str, String startStr, String endStr) {
        if (isEmpty(str)) {
            return EMPTY_STR;
        }
        int i = str.indexOf(startStr);
        int j = str.lastIndexOf(endStr);
        if (i < 0 || j < 0) {
            return EMPTY_STR;
        }
        //  between start and end
        return str.substring(i + startStr.length(), j);
    }

    /**
     * 是否空
     * */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 非空
     * */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 字符串防重复替换，多次调用只会替换一次
     * */
    public static String replaceNoRepeat(String str, String target, String replacement) {
        return str.replaceAll(target, replacement);
    }

    public static String replaceSuffix(String str, String target, String replacement) {
        if (isEmpty(str) || !str.endsWith(target)) {
            return str;
        }
        return removeSuffix(str, target) + replacement;
    }

    public static String removeSuffix(String str, String suffix) {
        if (isEmpty(str) || !str.endsWith(suffix)) {
            return str;
        }
        int index = str.lastIndexOf(suffix);
        return str.substring(0, index);
    }
    
}
