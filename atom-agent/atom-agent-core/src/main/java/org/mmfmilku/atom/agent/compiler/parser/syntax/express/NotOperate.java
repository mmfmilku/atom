package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void useImports(Map<String, String> importsMap) {
        expression.useImports(importsMap);
    }

    @Override
    public List<Expression> getBaseExpression() {
        return expression.getBaseExpression();
    }
}
