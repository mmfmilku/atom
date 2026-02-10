package org.mmfmilku.atom.agent.compiler.parser.syntax.express;


/**
 * 数组下标元素获取表达式，如：
 * arr[0]，strArr[i]，strArr[getIndex()]
 * */
public class ArrayElement implements NestedExpression {

    /**
     * 获取元素的数组表达式
     * */
    private Expression arrExp;

    /**
     * 获取元素对应的下标表达式
     * */
    private Expression elementIndexExp;

    public ArrayElement(Expression arrExp, Expression elementIndexExp) {
        this.arrExp = arrExp;
        this.elementIndexExp = elementIndexExp;
    }

    @Override
    public Expression[] orinChildren() {
        return new Expression[] {arrExp};
    }

    @Override
    public String getSourceCode() {
        return arrExp.getSourceCode() + "[" + elementIndexExp.getSourceCode() + "]";
    }

    public Expression getArrExp() {
        return arrExp;
    }

    public void setArrExp(Expression arrExp) {
        this.arrExp = arrExp;
    }

    public Expression getElementIndexExp() {
        return elementIndexExp;
    }

    public void setElementIndexExp(Expression elementIndexExp) {
        this.elementIndexExp = elementIndexExp;
    }
}
