package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhileStatement implements Statement {
    @Override
    public String getStatementSource() {
        // TODO
        GrammarUtil.notSupport();
        return null;
    }

    @Override
    public List<Expression> getAllExpression() {
        GrammarUtil.notSupport();
        return null;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        GrammarUtil.notSupport();
    }
}
