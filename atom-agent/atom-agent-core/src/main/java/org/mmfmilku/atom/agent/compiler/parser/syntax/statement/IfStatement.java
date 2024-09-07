package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

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
}
