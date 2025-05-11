package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

import java.util.*;

/**
 * 双目表达式
 * */
public class BinaryOperate implements NestedExpression {

    private String operator;

    private Expression[] children = new Expression[2];

    public BinaryOperate(Expression left, String operator, Expression right) {
        children[0] = left;
        children[1] = right;
        this.operator = operator;
    }

    public Expression getLeft() {
        return children[0];
    }

    public Expression getRight() {
        return children[1];
    }

    @Override
    public Expression[] orinChildren() {
        return children;
    }

    @Override
    public String getSourceCode() {
        return getLeft().getSourceCode() + GrammarUtil.surroundBlank(operator) + getRight().getSourceCode();
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        getLeft().useImports(importsMap);
        getRight().useImports(importsMap);
    }

    @Override
    public List<LeafExpression> getLeafExpression() {
        List<LeafExpression> leftExpression = getLeft().getLeafExpression();
        List<LeafExpression> rightExpression = getRight().getLeafExpression();
        List<LeafExpression> expressions = new ArrayList<>();
        expressions.addAll(leftExpression);
        expressions.addAll(rightExpression);
        return expressions;
    }

    @Override
    public List<Expression> getNested() {
        return Arrays.asList(children);
    }
}
