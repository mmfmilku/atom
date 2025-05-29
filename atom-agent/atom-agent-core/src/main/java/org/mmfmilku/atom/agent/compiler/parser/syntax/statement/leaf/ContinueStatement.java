package org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

public class ContinueStatement extends ExpStatement {

    public ContinueStatement() {
        super(null);
    }

    public ContinueStatement(Expression expression) {
        super(expression);
    }

    @Override
    public String getStatementSource() {
        return getExpression() == null ? "break" : "break " + getExpression().getSourceCode();
    }
}
