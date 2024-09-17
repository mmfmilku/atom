package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.HashMap;

public class IfStatement implements Statement {

    /**
     * if中的条件表达式
     * */
    private Expression condition;

    /**
     * if为 true 时执行的代码块
     * */
    private Statement trueStatement;

    /**
     * if为 false 时执行的代码块
     * */
    private Statement falseStatement;

    public IfStatement(Expression condition, Statement trueStatement) {
        this.condition = condition;
        this.trueStatement = trueStatement;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Statement getTrueStatement() {
        return trueStatement;
    }

    public void setTrueStatement(Statement trueStatement) {
        this.trueStatement = trueStatement;
    }

    public Statement getFalseStatement() {
        return falseStatement;
    }

    public void setFalseStatement(Statement falseStatement) {
        this.falseStatement = falseStatement;
    }

    @Override
    public String getStatementSource() {
        return "if (" + condition.getSourceCode() + ")"
                + trueStatement.getSourceCode()
                + (falseStatement == null ? "" : "else " + falseStatement.getSourceCode());
    }

    @Override
    public String getSourceCode() {
        return getStatementSource();
    }

    @Override
    public void useImports(HashMap<String, String> importsMap) {
        condition.useImports(importsMap);
        trueStatement.useImports(importsMap);
        if (falseStatement != null) {
            falseStatement.useImports(importsMap);
        }
    }
}
