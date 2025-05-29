package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 括号包裹的 优先执行表达式
 * */
public class PriorityExpression implements NestedExpression {

    private Expression expression;

    public PriorityExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        expression.useImports(importsMap);
    }

    @Override
    public String getSourceCode() {
        return "(" + expression.getSourceCode() + ")";
    }

    @Override
    public List<LeafExpression> getLeafExpression() {
        return expression.getLeafExpression();
    }

    @Override
    public List<Expression> getNested() {
        return Collections.singletonList(expression);
    }
}
