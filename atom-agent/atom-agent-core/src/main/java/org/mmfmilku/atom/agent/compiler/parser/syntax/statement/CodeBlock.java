package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

import java.util.ArrayList;
import java.util.HashMap;
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

    @Override
    public String getSourceCode() {
        return getStatementSource();
    }

    @Override
    public void useImports(HashMap<String, String> importsMap) {
        statements.forEach(statement -> statement.useImports(importsMap));
    }
}
