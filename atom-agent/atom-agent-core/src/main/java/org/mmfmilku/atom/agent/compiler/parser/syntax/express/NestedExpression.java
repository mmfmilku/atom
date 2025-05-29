package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.LinkedNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 树干节点表达式
 * */
public interface NestedExpression extends Expression {

    /**
     * 获取子表达式列表，不会递归获取到叶子节点
     * */
    default List<Expression> getNested() {
        return Arrays.asList(orinChildren());
    }

    @Override
    default void useImports(Map<String, String> importsMap) {
        for (Expression expression : getNested()) {
            expression.useImports(importsMap);
        }
    }

    @Override
    default List<LeafExpression> getLeafExpression() {
        return getNested()
                .stream()
                .map(Expression::getLeafExpression)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 获取原始子表达式数组
     * */
    default Expression[] orinChildren() {
        GrammarUtil.notSupport();
        return null;
    }

    @Override
    default List<LinkedNode> getChildren() {
        return new ArrayList<>(getNested());
    }
}
