package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.LinkedNode;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ExpressionOperate;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ImportUse;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Statement extends Node, ImportUse, ExpressionOperate, LinkedNode {

    String getStatementSource();

    default void setModifier(Modifier modifier) {

    }

    @Override
    default String getSourceCode() {
        return getStatementSource() + ";";
    }

    @Override
    default List<LinkedNode> getChildren() {
        return new ArrayList<>(getNestedExp());
    }

    /**
     * 获取内部包含的表达式，不会递归获取
     * */
    default List<Expression> getNestedExp() {
        return Collections.emptyList();
    }
}
