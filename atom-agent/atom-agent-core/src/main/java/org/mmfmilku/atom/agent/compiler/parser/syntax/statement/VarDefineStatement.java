package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

/***
 * 变量定义语句
 */
public class VarDefineStatement extends Statement {

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
}
