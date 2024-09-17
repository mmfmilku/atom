package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

public class ReturnStatement extends ExpStatement {

    public ReturnStatement(Expression expression) {
        super(expression);
    }

    @Override
    public String getStatementSource() {
        return "return " + super.getStatementSource();
    }
}
