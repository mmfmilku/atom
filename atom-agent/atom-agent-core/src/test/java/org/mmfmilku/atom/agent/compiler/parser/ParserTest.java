package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.util.FileUtils;

import javax.annotation.Resource;
import java.io.IOException;

import static org.junit.Assert.*;

@SuppressWarnings("f")
@Resource
public class ParserTest {

    public void testNo() {

    }

    @Test
    public void testParser() throws IOException {
        String s = FileUtils.readText("F:\\dev\\project\\atom\\atom-agent\\atom-agent-core\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\compiler\\parser\\ParserTest.java");
        Lexer lexer = new Lexer(s);
        lexer.execute();
        Node execute = new Parser(lexer).execute();
        System.out.println(execute);
        int a = 1;
        int b = 3;
        a++;
        --b;
        int c = a++ + b;
        System.out.println(a + b - c);
        if (c > 0) {
            System.out.println("c大于0");
        }
        System.out.println("-----------------源码开始----------------");
        System.out.println(execute.getSourceCode().trim());
    }

    @Test
    public void testExpressionParse() {
        String text = "a + b + c";
        Expression e1 = CompilerUtil.parseExpression(text);
        System.out.println(e1.getSourceCode());
        assertEquals(text, e1.getSourceCode());

        System.out.println(CompilerUtil
                .parseExpression("map.put(\"k\", bb)")
                .getSourceCode());

        System.out.println(CompilerUtil
                .parseExpression("data.get(0).getId().equals(id)")
                .getSourceCode());

        System.out.println(CompilerUtil
                .parseExpression("m1(p1, p2, m2()) + m3()")
                .getSourceCode());
    }

}