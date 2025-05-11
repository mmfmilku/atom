package org.mmfmilku.atom.agent.compiler.parser.syntax.statement;

import org.mmfmilku.atom.agent.compiler.parser.syntax.CodeNode;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ExpressionOperate;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ImportUse;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

public interface Statement extends Node, ImportUse, ExpressionOperate, CodeNode {

    String getStatementSource();

    default void setModifier(Modifier modifier) {

    }

    @Override
    default String getSourceCode() {
        return getStatementSource() + ";";
    }

}
