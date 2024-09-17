package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

import java.util.HashMap;

public class ForStatement implements Statement {
    @Override
    public String getStatementSource() {
        // TODO
        GrammarUtil.notSupport();
        return null;
    }

    @Override
    public void useImports(HashMap<String, String> importsMap) {
        GrammarUtil.notSupport();
    }
}
