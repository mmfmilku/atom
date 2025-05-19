package org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExpStatement implements LeafStatement {

    private Expression expression;

    public ExpStatement(Expression expression) {
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
        return expression.getSourceCode();
    }

    @Override
    public List<Expression> getAllExpression() {
        return expression == null ? Collections.emptyList() : expression.getAllExpression();
    }

    @Override
    public List<Expression> getNestedExp() {
        return expression == null ? Collections.emptyList() : Collections.singletonList(expression);
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        if (expression != null)
            expression.useImports(importsMap);
    }
}
