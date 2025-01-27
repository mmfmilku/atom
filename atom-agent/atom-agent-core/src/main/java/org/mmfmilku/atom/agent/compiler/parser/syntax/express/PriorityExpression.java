package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.Map;

public class PriorityExpression implements Expression {

    private Expression expression;

    public PriorityExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        expression.useImports(importsMap);
    }

    @Override
    public String getSourceCode() {
        return "(" + expression.getSourceCode() + ")";
    }
}
