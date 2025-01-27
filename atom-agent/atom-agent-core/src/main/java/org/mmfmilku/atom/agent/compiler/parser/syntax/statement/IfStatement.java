package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IfStatement implements SpecialStatement {

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
    public List<Expression> getAllExpression() {
        List<Expression> all = new ArrayList<>();
        all.add(condition);
        all.addAll(trueStatement.getAllExpression());
        if (falseStatement != null) {
            all.addAll(falseStatement.getAllExpression());
        }
        return all;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        condition.useImports(importsMap);
        trueStatement.useImports(importsMap);
        if (falseStatement != null) {
            falseStatement.useImports(importsMap);
        }
    }
}
