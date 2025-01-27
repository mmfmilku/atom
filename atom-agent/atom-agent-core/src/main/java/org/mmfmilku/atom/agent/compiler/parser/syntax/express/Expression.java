package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ExpressionOperate;
import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.ImportUse;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

import java.util.Collections;
import java.util.List;

public interface Expression extends Node, ImportUse, ExpressionOperate {

    @Override
    default List<Expression> getAllExpression() {
        return getBaseExpression();
    }

    default List<Expression> getBaseExpression() {
        return Collections.singletonList(this);
    }

}
