package org.mmfmilku.atom.agent.compiler.parser.syntax;

/**
 * 描述定义的类型信息
 * */
public class TypeDefine implements Node {

    private String type;

    private Generics generics;

    private boolean arr;

    public TypeDefine(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Generics getGenerics() {
        return generics;
    }

    public void setGenerics(Generics generics) {
        this.generics = generics;
    }

    public boolean isArr() {
        return arr;
    }

    public void setArr(boolean arr) {
        this.arr = arr;
    }

    @Override
    public String getSourceCode() {
        return null;
    }
}
