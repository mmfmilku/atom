package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 变量赋值语句
 * */
public class VarAssignStatement implements Statement {

    private String varName;
    private Expression assignExpression;

    public VarAssignStatement(String varName, Expression assignExpression) {
        this.varName = varName;
        this.assignExpression = assignExpression;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public Expression getAssignExpression() {
        return assignExpression;
    }

    public void setAssignExpression(Expression assignExpression) {
        this.assignExpression = assignExpression;
    }

    @Override
    public String getStatementSource() {
        return varName + " = " + assignExpression.getSourceCode();
    }

    @Override
    public List<Expression> getAllExpression() {
        return Collections.singletonList(assignExpression);
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        assignExpression.useImports(importsMap);
    }
}
