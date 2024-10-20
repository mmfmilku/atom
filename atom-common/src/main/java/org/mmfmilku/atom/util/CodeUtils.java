package org.mmfmilku.atom.util;

import org.mmfmilku.atom.consts.CodeConst;

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

    public static String toJavaFilePath(String fullClassName) {
        return toFileName(fullClassName) + CodeConst.JAVA_FILE_SUFFIX;
    }

    public static String toClassFilePath(String fullClassName) {
        return toFileName(fullClassName) + CodeConst.CLASS_FILE_SUFFIX;
    }

    public static String toFileName(String fullClassName) {
        return fullClassName.replaceAll("\\.", "\\" + File.separator);
    }

    public static String toClassName(String filePathName) {
        String className = filePathName.replaceAll("/", "\\.");
        className = StringUtils.removeSuffix(className, CodeConst.CLASS_FILE_SUFFIX);
        className = StringUtils.removeSuffix(className, CodeConst.JAVA_FILE_SUFFIX);
        return className;
    }
}
