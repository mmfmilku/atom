package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/***
 * 变量定义语句
 */
public class VarDefineStatement implements Statement {

    private String varType;
    private String varName;
    private Expression assignExpression;

    public VarDefineStatement(String varType, String varName) {
        this.varType = varType;
        this.varName = varName;
    }

    public String getVarType() {
        return varType;
    }

    public void setVarType(String varType) {
        this.varType = varType;
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
        String assignValueStr = "";
        if (assignExpression != null) {
            assignValueStr = " = " + assignExpression.getSourceCode();
        }
        return varType + " " + varName + assignValueStr;
    }

    @Override
    public List<Expression> getAllExpression() {
        return assignExpression == null ?
                Collections.emptyList() : Collections.singletonList(assignExpression);
    }

    @Override
    public void useImports(HashMap<String, String> importsMap) {
        setVarType(importsMap.getOrDefault(varType, varType));
        if (assignExpression != null) {
            assignExpression.useImports(importsMap);
        }
    }
}
