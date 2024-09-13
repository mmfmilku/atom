package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

import java.util.List;

public class Class implements Node {

    /**
     * 注解
     * */
    private List<Annotation> annotations;

    /**
     * 类名
     * */
    private String className;

    /**
     * 类全名
     * */
    private String classFullName;

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

    public Class(String className) {
        this.className = className;
    }

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    @Override
    public String getSourceCode() {
        return GrammarUtil.getLinesCode(annotations)
                + "\n"
                + modifier.getSourceCode() + " class " + getClassName() + " {"
                + GrammarUtil.getLinesCode(methods)
                + "}"
                ;
    }
}
