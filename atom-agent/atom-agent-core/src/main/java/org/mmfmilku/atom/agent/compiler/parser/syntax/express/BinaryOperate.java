package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

import java.util.*;

/**
 * 双目表达式
 * */
public class BinaryOperate implements NestedExpression {

    private Expression left;
    private String operator;
    private Expression right;

    public BinaryOperate(Expression left, String operator, Expression right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String getSourceCode() {
        return left.getSourceCode() + GrammarUtil.surroundBlank(operator) + right.getSourceCode();
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        left.useImports(importsMap);
        right.useImports(importsMap);
    }

    @Override
    public List<LeafExpression> getLeafExpression() {
        List<LeafExpression> leftExpression = left.getLeafExpression();
        List<LeafExpression> rightExpression = right.getLeafExpression();
        List<LeafExpression> expressions = new ArrayList<>();
        expressions.addAll(leftExpression);
        expressions.addAll(rightExpression);
        return expressions;
    }

    @Override
    public List<Expression> getNested() {
        return Arrays.asList(left, right);
    }
}
