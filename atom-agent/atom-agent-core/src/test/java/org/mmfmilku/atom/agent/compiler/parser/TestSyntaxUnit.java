package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;

import static org.junit.Assert.*;

public class TestSyntaxUnit {

    @Test
    public void testTernaryParse() {
        String code = "b1 && b2 ? i == 1 ? 1 : 2 : l == 0 ? 3 : 4";
        Expression expression = CompilerUtil.parseExpression(code);
        System.out.println(expression.getSourceCode());
    }

}
