package org.mmfmilku.atom.util;

import java.io.File;

public class CodeUtils {

    private static final char[] INVALID_CODE_CHAR = {'\b', '\r', '\n', ' '};

    /**
     * 是否空代码块 {}
     * */
    public static boolean isEmptyBlock(String src) {
        // TODO 优化
        return src.substring(src.indexOf("{") + 1, src.lastIndexOf("}")).trim().length() == 0;
    }

    public static boolean valid(char c) {
        return c != ' ' && c != '\n' && c != '\r';
    }

    public static String readCode (String inputCode) {
        StringBuilder codeBuilder = new StringBuilder();
        for (char c : inputCode.toCharArray()) {
            
            codeBuilder.append(c);
        }
        return codeBuilder.toString();
    }

    public static String toFileName(String fullClassName) {
        return fullClassName.replaceAll("\\.", "\\" + File.separator);
    }
}
