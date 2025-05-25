package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.Generics;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;

import java.util.List;
import java.util.Map;

public class ArrayCreate implements NestedExpression {

    private String className;

    private Generics generics;

    private int arrSize;

    private Expression[] initData;

    @Override
    public List<Expression> getNested() {
        return null;
    }

    @Override
    public List<LeafExpression> getLeafExpression() {
        return null;
    }

    @Override
    public String getSourceCode() {
        return null;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {

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
