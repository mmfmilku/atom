package org.mmfmilku.atom.agent.compiler.lexer;

/**
 * TokenType
 *
 * @author chenxp
 * @date 2024/8/6:14:48
 */
public enum TokenType {

    /**
     * 单词
     * */
    Words(" "),
    /**
     * 字符串
     * */
    String(""),
    /**
     * 数字
     * */
    Number(" "),
    /**
     * 括号
     * */
    Paren(""),
    /**
     * 大括号
     * */
    Brace("\n"),
    /**
     * 符号
     * */
    Symbol(" "),
    /**
     * 换行符
     * */
    LineSymbol(" "),
    /**
     * 块注释
     * */
    BlockComment("\n"),
    /**
     * 行注释
     * */
    Comment("\n"),
    ;

    TokenType(String showPrefix, String showSuffix) {
        this.showPrefix = showPrefix;
        this.showSuffix = showSuffix;
    }

    TokenType(String showSuffix) {
        this.showPrefix = "";
        this.showSuffix = showSuffix;
    }

    /**
     * 代码展示前缀
     * */
    private String showPrefix;

    /**
     * 代码展示后缀
     * */
    private String showSuffix;

    public java.lang.String getShowPrefix() {
        return showPrefix;
    }

    public java.lang.String getShowSuffix() {
        return showSuffix;
    }
}
