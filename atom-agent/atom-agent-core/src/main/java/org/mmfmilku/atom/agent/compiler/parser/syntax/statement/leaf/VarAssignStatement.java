package org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.NestedExpression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.LinkedNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 变量赋值语句
 * */
public class VarAssignStatement implements LeafStatement, NestedExpression {

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
        return assignExpression.getAllExpression();
    }

    @Override
    public List<Expression> getNestedExp() {
        return Collections.singletonList(assignExpression);
    }

    @Override
    public List<Expression> getNested() {
        return Collections.singletonList(assignExpression);
    }

    @Override
    public List<LinkedNode> getChildren() {
        return new ArrayList<>(getNestedExp());
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        assignExpression.useImports(importsMap);
    }

}
