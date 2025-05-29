package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 变量赋值语句
 * */
public class VarAssign implements NestedExpression {

    // TODO 改为标识符
    private String varName;
    private Expression assignExpression;

    public VarAssign(String varName, Expression assignExpression) {
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
    public List<Expression> getAllExpression() {
        return assignExpression.getAllExpression();
    }

    @Override
    public List<Expression> getNested() {
        return Collections.singletonList(assignExpression);
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        assignExpression.useImports(importsMap);
    }

    @Override
    public String getSourceCode() {
        return varName + " = " + assignExpression.getSourceCode();
    }
}
