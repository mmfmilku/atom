package org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import java.util.Collections;
import java.util.List;

public interface LeafExpression extends Expression {

    @Override
    default List<Expression> getBaseExpression() {
        return Collections.singletonList(this);
    }

}
