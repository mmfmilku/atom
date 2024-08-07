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
    Words,
    /**
     * 字符串
     * */
    String,
    /**
     * 数字
     * */
    Number,
    /**
     * 符号
     * */
    Symbol,
    /**
     * 操作符
     * */
    Operator,
    /**
     * 括号 ()
     * */
    Paren,
    /**
     * 大括号 {}
     * */
    Braces,
    /**
     * 尖括号 <>
     * */
    Angle;
    
//    KEYWORDS,
//    /**
//     * 所属包关键字 package
//     * */
//    PACKAGE,
//    /**
//     * 类导入关键字 import
//     * */
//    IMPORT,
//    /**
//     * 标识符 导入的包 xx.xx.XXX
//     * */
//    IMPORT_PATH,
//    /**
//     * 类型关键字 class,enum
//     * */
//    CLASS_DESC,
//    /**
//     * 权限范围关键字 public,private,protect
//     * */
//    AUTH_DESC,
//    /**
//     * 标识符
//     * */
//    IDENTIFY;
    
    private String type;
    private String value;
    
}
