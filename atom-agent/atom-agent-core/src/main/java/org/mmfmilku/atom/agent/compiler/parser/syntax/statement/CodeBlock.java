package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

import java.util.*;
import java.util.stream.Collectors;

public class CodeBlock implements SpecialStatement {

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
    public List<Expression> getAllExpression() {
        return statements.stream()
                .map(Statement::getAllExpression)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        statements.forEach(statement -> statement.useImports(importsMap));
    }
}
