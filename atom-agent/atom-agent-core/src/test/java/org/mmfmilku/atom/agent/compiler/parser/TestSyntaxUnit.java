package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.TernaryOperate;

import static org.junit.Assert.*;

public class TestSyntaxUnit {

    @Test
    public void testTernaryParse() {
        String code = "b1 && b2 ? i == 1 ? 1 : 2 : l == 0 ? 3 : 4";
        Expression expression = CompilerUtil.parseExpression(code);
        System.out.println(expression.getSourceCode());
        assertTrue(code + "解析结果应为三目表达式", expression instanceof TernaryOperate);
        TernaryOperate ternaryOperate = (TernaryOperate) expression;
        assertEquals("b1 && b2 ? i == 1 ? 1 : 2 : l == 0 ? 3 : 4",
                ternaryOperate.getSourceCode());
        assertEquals("b1 && b2", ternaryOperate.getBoolExp().getSourceCode());
        assertEquals("i == 1 ? 1 : 2", ternaryOperate.getTrueExp().getSourceCode());
        assertEquals("l == 0 ? 3 : 4", ternaryOperate.getFalseExp().getSourceCode());
    }

}
