package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.ClassType;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.AccessPrivilege;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.util.StringUtils;

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
     * 修饰符
     * */
    private Modifier modifier;

    /**
     * 继承类
     * */
    private String superClass;

    /**
     * 实现类
     * */
    private List<String> implementClasses;

    // TODO 内部类,静态非静态？
    private List<String> innerClasses;

    // TODO 静态代码块

    private List<Member> members;

    private List<Method> constructors;

    private List<Method> methods;

    public Modifier getModifier() {
        return modifier;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public List<Method> getConstructors() {
        return constructors;
    }

    public void setConstructors(List<Method> constructors) {
        this.constructors = constructors;
    }

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

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public List<String> getImplementClasses() {
        return implementClasses;
    }

    public void setImplementClasses(List<String> implementClasses) {
        this.implementClasses = implementClasses;
    }

    public List<String> getInnerClasses() {
        return innerClasses;
    }

    public void setInnerClasses(List<String> innerClasses) {
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
                + GrammarUtil.getSentenceCode(modifier.getSourceCode(), "class", getClassName())
                + (StringUtils.isEmpty(superClass) ? ""
                    : " extends " + superClass)
                + (implementClasses == null || implementClasses.size() == 0 ? ""
                    : " implements " + String.join(", ", implementClasses))
                + " {"
                + GrammarUtil.getLinesCode(members)
                + GrammarUtil.getLinesCode(constructors)
                + GrammarUtil.getLinesCode(methods)
                + "}"
                ;
    }
}
