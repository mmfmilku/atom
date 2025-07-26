package org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf;

import org.mmfmilku.atom.agent.compiler.parser.syntax.Generics;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/***
 * 变量定义语句
 */
public class VarDefineStatement implements LeafStatement {

    private String varType;
    private String varName;
    private boolean arr;
    private int arrDimension;
    private Expression assignExpression;
    private Generics generics;

    public VarDefineStatement(String varType, String varName) {
        this.varType = varType;
        this.varName = varName;
    }

    public Generics getGenerics() {
        return generics;
    }

    public void setGenerics(Generics generics) {
        this.generics = generics;
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
                Collections.emptyList() : assignExpression.getAllExpression();
    }

    @Override
    public List<Expression> getNestedExp() {
        return assignExpression == null ?
                Collections.emptyList() : Collections.singletonList(assignExpression);
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        setVarType(importsMap.getOrDefault(varType, varType));
        if (assignExpression != null) {
            assignExpression.useImports(importsMap);
        }
    }
}
