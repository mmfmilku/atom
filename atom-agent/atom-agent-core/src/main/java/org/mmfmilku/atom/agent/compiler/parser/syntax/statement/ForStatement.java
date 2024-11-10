package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForStatement extends LoopStatement {

    private Statement beforeStatement;

    private Statement afterStatement;

    public ForStatement(Statement beforeStatement, Statement afterStatement,
                        Expression loopCondition, CodeBlock loopBody) {
        super(loopCondition, loopBody);
        this.beforeStatement = beforeStatement;
        this.afterStatement = afterStatement;
    }

    public Statement getBeforeStatement() {
        return beforeStatement;
    }

    public void setBeforeStatement(Statement beforeStatement) {
        this.beforeStatement = beforeStatement;
    }

    public Statement getAfterStatement() {
        return afterStatement;
    }

    public void setAfterStatement(Statement afterStatement) {
        this.afterStatement = afterStatement;
    }

    @Override
    public String getStatementSource() {
        return GrammarUtil.getSentenceCode("for",
                "(",
                beforeStatement.getSourceCode(),
                loopCondition.getSourceCode(), ";",
                afterStatement.getSourceCode(),
                ")",
                loopBody.getSourceCode()
        );
    }

    @Override
    public String getSourceCode() {
        return getStatementSource();
    }

    @Override
    public List<Expression> getAllExpression() {
        List<Expression> expressions = new ArrayList<>();
        expressions.addAll(beforeStatement.getAllExpression());
        expressions.addAll(afterStatement.getAllExpression());
        expressions.addAll(super.getAllExpression());
        return expressions;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        beforeStatement.useImports(importsMap);
        afterStatement.useImports(importsMap);
        super.useImports(importsMap);
    }
}
