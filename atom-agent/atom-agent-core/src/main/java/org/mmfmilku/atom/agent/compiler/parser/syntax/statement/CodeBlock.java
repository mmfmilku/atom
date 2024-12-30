package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

import java.util.*;
import java.util.stream.Collectors;

public class CodeBlock implements SpecialStatement {

    public static CodeBlock EMPTY = new CodeBlock();

    private List<Statement> statements = new ArrayList<>();

    private Modifier modifier = Modifier.DEFAULT;

    public Modifier getModifier() {
        return modifier;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String getStatementSource() {
        if (modifier != null && modifier.isStaticDeco()) {
            return "static {" + GrammarUtil.getLinesCode(statements) + "}";
        }
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
