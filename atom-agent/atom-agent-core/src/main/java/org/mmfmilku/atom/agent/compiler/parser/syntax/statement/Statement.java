package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.ImportUse;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.List;

public interface Statement extends Node, ImportUse {

    String getStatementSource();

    List<Expression> getAllExpression();

    @Override
    default String getSourceCode() {
        return getStatementSource() + ";";
    }
}
