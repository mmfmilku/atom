package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.HashMap;

public class ExpStatement implements Statement {

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
    public void useImports(HashMap<String, String> importsMap) {
        expression.useImports(importsMap);
    }
}