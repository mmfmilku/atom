package org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

public class BreakStatement extends ExpStatement {

    public BreakStatement() {
        super(null);
    }

    public BreakStatement(Expression expression) {
        super(expression);
    }

    @Override
    public String getStatementSource() {
        return getExpression() == null ? "continue" : "continue " + getExpression().getSourceCode();
    }
}
