package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.HashMap;

/**
 * 取反表达式
 * */
public class NotOperate implements Expression {

    private Expression expression;

    public NotOperate(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String getSourceCode() {
        return "!" + expression.getSourceCode();
    }

    @Override
    public void useImports(HashMap<String, String> importsMap) {
        expression.useImports(importsMap);
    }
}