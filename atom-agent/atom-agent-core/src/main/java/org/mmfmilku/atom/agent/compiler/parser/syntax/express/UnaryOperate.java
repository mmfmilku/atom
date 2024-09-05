package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

/**
 * 单目表达式
 * */
public class UnaryOperate implements Expression {

    private String operator;
    private Expression expression;

    public UnaryOperate(String operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
