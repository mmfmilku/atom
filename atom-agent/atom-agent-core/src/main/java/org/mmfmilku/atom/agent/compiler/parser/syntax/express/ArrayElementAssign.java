package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

/**
 * 数组元素赋值
 * */
public class ArrayElementAssign implements NestedExpression {

    private ArrayElement arrayElement;

    /**
     * 赋值表达式
     * */
    private Expression assignExpression;

    public ArrayElementAssign(ArrayElement arrayElement, Expression assignExpression) {
        this.arrayElement = arrayElement;
        this.assignExpression = assignExpression;
    }

    @Override
    public String getSourceCode() {
        return arrayElement.getSourceCode() + " = " + assignExpression.getSourceCode();
    }

    @Override
    public Expression[] orinChildren() {
        return new Expression[]{arrayElement, assignExpression};
    }

    public ArrayElement getArrayElement() {
        return arrayElement;
    }

    public void setArrayElement(ArrayElement arrayElement) {
        this.arrayElement = arrayElement;
    }

    public Expression getAssignExpression() {
        return assignExpression;
    }

    public void setAssignExpression(Expression assignExpression) {
        this.assignExpression = assignExpression;
    }


}
