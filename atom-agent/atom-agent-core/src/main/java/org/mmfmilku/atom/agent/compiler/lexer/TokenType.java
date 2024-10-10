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
     * 字符
     * */
    Character(""),
    /**
     * 数字
     * */
    Number(" "),
    /**
     * 括号
     * */
    LParen("", "", "("),
    RParen("", "", ")"),
    /**
     * 中括号
     * */
    LBracket("", "", "["),
    RBracket("", "", "]"),
    /**
     * 大括号
     * */
    LBrace("", "", "{"),
    RBrace("", "", "}"),
    /**
     * 尖括号
     * */
    LAngle("", "", "<"),
    RAngle("", "", ">"),
    /**
     * 符号
     * */
    Symbol(""),
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

    TokenType(String codeSuffix) {
        this.codePrefix = "";
        this.codeSuffix = codeSuffix;
    }
    
    TokenType(String codePrefix, String codeSuffix) {
        this.codePrefix = codePrefix;
        this.codeSuffix = codeSuffix;
    }

    TokenType(String codePrefix, String codeSuffix, String fixValue) {
        this.fixValue = fixValue;
        this.codePrefix = codePrefix;
        this.codeSuffix = codeSuffix;
    }

    /**
     * 类型固定值
     * */
    private String fixValue;

    /**
     * 代码转换前缀
     * */
    private String codePrefix;

    /**
     * 代码转换后缀
     * */
    private String codeSuffix;

    public String getFixValue() {
        return fixValue;
    }

    public String getCodePrefix() {
        return codePrefix;
    }

    public String getCodeSuffix() {
        return codeSuffix;
    }

}
