package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReturnStatement implements Statement {

    private Expression expression;

    public ReturnStatement() {
    }

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String getStatementSource() {
        return expression == null ? "return" : "return " + expression.getSourceCode();
    }

    @Override
    public List<Expression> getAllExpression() {
        return expression == null ? Collections.emptyList() : Collections.singletonList(expression);
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        if (expression != null)
            expression.useImports(importsMap);
    }
}
