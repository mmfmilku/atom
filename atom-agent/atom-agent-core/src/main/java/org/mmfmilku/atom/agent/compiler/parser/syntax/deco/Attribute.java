package org.mmfmilku.atom.agent.compiler.parser.syntax.deco;

/**
 * 描述了一些关键字属性如：
 * 修饰符、同步、静态等
 * public static final synchronized
 * */
public class Attribute {

    public static final Attribute DEFAULT = new Attribute();

    Modifier modifier = Modifier.DEFAULT;

    /**
     * static 关键字修饰
     * */
    boolean staticDeco;

    /**
     * abstract 关键字修饰
     * */
    boolean abstractDeco;

    /**
     * final 关键字修饰
     * */
    boolean finalDeco;

    /**
     * synchronized 关键字修饰
     * */
    boolean synchronizedDeco;

    /**
     * transient 关键字修饰
     * */
    boolean transientDeco;

    /**
     * volatile 关键字修饰
     * */
    boolean volatileDeco;
}
