package org.mmfmilku.atom.agent.compiler.parser.syntax;

import java.util.List;

public class Class implements Node {

    /**
     * 注解
     * */
    private List<Annotation> annotations;

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

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public Class getSuperClass() {
        return superClass;
    }

    public void setSuperClass(Class superClass) {
        this.superClass = superClass;
    }

    public List<Class> getImplementClasses() {
        return implementClasses;
    }

    public void setImplementClasses(List<Class> implementClasses) {
        this.implementClasses = implementClasses;
    }

    public List<Class> getInnerClasses() {
        return innerClasses;
    }

    public void setInnerClasses(List<Class> innerClasses) {
        this.innerClasses = innerClasses;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }
}
