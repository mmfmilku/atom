package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Generics;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayCreate implements NestedExpression {

    private String className;

    private Generics generics;

    private int arrSize;

    private Expression[] initData = new Expression[0];

    public ArrayCreate(String className) {
        this.className = className;
    }

    @Override
    public Expression[] orinChildren() {
        return getInitData();
    }

    @Override
    public String getSourceCode() {
        String showSize = "" + arrSize;
        String showInitData = "";
        if (initData != null && initData.length > 0) {
            showSize = "";
            showInitData = "{" + Stream.of(initData)
                    .map(Expression::getSourceCode)
                    .collect(Collectors.joining(", "))
                    + "}";
        }
        return "new " + className + "[" + showSize + "]" + showInitData;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Generics getGenerics() {
        return generics;
    }

    public void setGenerics(Generics generics) {
        this.generics = generics;
    }

    public Expression[] getInitData() {
        return initData;
    }

    public void setInitData(Expression[] initData) {
        this.initData = initData;
    }

    public int getArrSize() {
        return arrSize;
    }

    public void setArrSize(int arrSize) {
        this.arrSize = arrSize;
    }
}
