package org.mmfmilku.atom.agent.compiler.parser.syntax;

import java.util.List;

public class Class implements Node {

    /**
     * 类名
     * */
    private String value;

    /**
     * 类类型
     * */
    private ClassType classType;

    /**
     * 访问权限
     * */
    private Modifier modifier;

    /**
     * 继承类
     * */
    private Class superClass;

    /**
     * 实现类
     * */
    private List<Class> implementClasses;

    // TODO 内部类,静态非静态？
    private List<Class> innerClasses;

    // TODO 静态代码块

    private List<Member> members;

    private List<Method> methods;


}
