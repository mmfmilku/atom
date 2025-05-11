package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.LinkedNode;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ExpressionOperate;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ImportUse;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

import java.util.List;

public interface Expression extends Node, ImportUse, ExpressionOperate, LinkedNode {

    @Override
    default List<Expression> getAllExpression() {
        return getBaseExpression();
    }

    List<Expression> getBaseExpression();

}
