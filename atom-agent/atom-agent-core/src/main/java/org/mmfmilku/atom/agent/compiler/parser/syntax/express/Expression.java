package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.CodeNode;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ExpressionOperate;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ImportUse;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

import java.util.List;

public interface Expression extends Node, ImportUse, ExpressionOperate, CodeNode {

    @Override
    default List<Expression> getAllExpression() {
        return getBaseExpression();
    }

    List<Expression> getBaseExpression();

}
