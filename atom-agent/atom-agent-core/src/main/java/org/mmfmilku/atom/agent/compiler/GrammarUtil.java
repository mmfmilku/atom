package org.mmfmilku.atom.agent.compiler;

public class GrammarUtil {

    /**
     * 运算符
     * */
    public static boolean isOperator(String value) {
        return "+".equals(value) ||
                "-".equals(value) ||
                "*".equals(value) ||
                "/".equals(value) ||
                "&".equals(value) ||
                "|".equals(value) ||
                "^".equals(value)
                ;
    }

    /**
     * 语句关键字
     * */
    public static boolean isCodeKeywords(String value) {
        // TODO  do {} while() , synchronized, return
        return "for".equals(value)
                || "while".equals(value)
                || "if".equals(value)
                || "new".equals(value);
    }

}
