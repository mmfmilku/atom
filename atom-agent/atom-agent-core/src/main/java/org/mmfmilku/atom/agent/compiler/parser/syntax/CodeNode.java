package org.mmfmilku.atom.agent.compiler.parser.syntax;

import java.util.Collections;
import java.util.List;

/**
 * 具体代码的语法节点
 * 用于语句、表达式维护节点间的引用关系
 * */
public interface CodeNode {

    default CodeNode getParent() {
        return null;
    }

    default List<CodeNode> getChildren() {
        return Collections.emptyList();
    }

}
