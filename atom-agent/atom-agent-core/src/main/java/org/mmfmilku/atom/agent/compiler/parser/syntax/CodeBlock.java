package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

import java.util.ArrayList;
import java.util.List;

public class CodeBlock implements Statement {

    private List<Statement> statements = new ArrayList<>();

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String getStatementSource() {
        return "{" + GrammarUtil.getLinesCode(statements) + "}";
    }
}
