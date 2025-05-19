package org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReturnStatement extends ExpStatement {

    public ReturnStatement() {
        super(null);
    }

    public ReturnStatement(Expression expression) {
        super(expression);
    }

    @Override
    public String getStatementSource() {
        return getExpression() == null ? "return" : "return " + getExpression().getSourceCode();
    }

}
