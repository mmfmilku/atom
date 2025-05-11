package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.LinkedNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 嵌套语句
 * 非单行类语句，如if、while语句，语句块等
 * 此类语句源码无需以;结尾
 * */
public interface NestedStatement extends Statement {

    @Override
    default String getSourceCode() {
        return getStatementSource();
    }

    @Override
    default List<LinkedNode> getChildren() {
        List<LinkedNode> children = new ArrayList<>(getNestedExp());
        children.addAll(getNested());
        return children;
    }

    /**
     * 获取子语句列表，不会递归获取
     * */
    List<Statement> getNested();
}
