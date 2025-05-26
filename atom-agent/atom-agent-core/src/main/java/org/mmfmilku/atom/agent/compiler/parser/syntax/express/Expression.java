package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.LinkedNode;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ExpressionOperate;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ImportUse;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

import java.util.ArrayList;
import java.util.List;

public interface Expression extends Node, ImportUse, ExpressionOperate, LinkedNode {

    @Override
    @Deprecated
    default List<Expression> getAllExpression() {
        return new ArrayList<>(getLeafExpression());
    }

    /**
     * 获取叶子节点表达式
     * */
    List<LeafExpression> getLeafExpression();

}
