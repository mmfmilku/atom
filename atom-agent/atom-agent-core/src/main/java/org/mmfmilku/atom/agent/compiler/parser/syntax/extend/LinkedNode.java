package org.mmfmilku.atom.agent.compiler.parser.syntax.extend;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

import java.util.Collections;
import java.util.List;

/**
 * 具体代码的语法节点
 * 用于语句、表达式维护节点间的引用关系
 * */
public interface LinkedNode {

    default LinkedNode getParent() {
        GrammarUtil.notSupport();
        return null;
    }

    default void setParent(LinkedNode node) {
        GrammarUtil.notSupport();
    }

    default List<LinkedNode> getChildren() {
        GrammarUtil.notSupport();
        return Collections.emptyList();
    }

}
