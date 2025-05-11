package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.LinkedNode;

import java.util.ArrayList;
import java.util.List;

public interface NestedExpression extends Expression {

    /**
     * 获取子表达式列表，不会递归获取到叶子节点
     * */
    List<Expression> getNested();

    @Override
    default List<LinkedNode> getChildren() {
        return new ArrayList<>(getNested());
    }
}
