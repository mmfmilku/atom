package org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

public class ThrowStatement extends ExpStatement {

    public ThrowStatement(Expression expression) {
        super(expression);
    }

    @Override
    public String getStatementSource() {
        return getExpression() == null ? "throw" : "throw " + getExpression().getSourceCode();
    }

}
