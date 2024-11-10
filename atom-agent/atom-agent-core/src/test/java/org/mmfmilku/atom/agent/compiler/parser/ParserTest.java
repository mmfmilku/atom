package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler
        .CompilerUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.aa.TestFile1;
import org.mmfmilku.atom.agent.compiler.parser.aa.TestFile2;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler
        .parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.util.TestUtil;
import org.mmfmilku.atom.util.FileUtils;

import javax.annotation.Resource;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * 语法解析测试
 * */
@SuppressWarnings("f")
@Resource
public class ParserTest implements Serializable, Closeable {

    int a = 1;
    String b = "";

    public ParserTest() {
        this.a = 0;
        this.b.equals("");
    }

    private void m1(String a, int b) {
        this.b = a;
        System.out.println(a);
        if (b == 0) {
            System.out.println("b等于零");
        }
        this.testNo();
    }

    @Test
    public void testNo() {
        return;
    }

    @Test
    public void testParser() throws IOException {
        // 获取用户目录
        String s = FileUtils.readText(System.getProperty("user.dir") + "\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\compiler\\parser\\ParserTest.java");
        Lexer lexer = new Lexer(s);
        lexer.execute();
//        System.out
//                .println(lexer.showCode(true, true));
        org.mmfmilku.atom.agent.compiler
                .parser.syntax.Node execute = new Parser(lexer)
                .execute();
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
        if (c != 0) {
            if (c == 0) {
                if (c <= 0) {
                    System.out.println("c大于0");
                }
            }
        }
        if (1 == 1) {
            if (2 >= 1) {
                if (3 != 2) {

                }
            } else if (a + b > 1) {
                if (1 + 3 >= 2) {}
            } else {
                System.out.println(3);
            }
        }
        System.out.println("ttt");
        System.out.println("ttt'a");
        System.out.println("ttt\\b");
        System.out.println("ttt\"c");
        System.out.println("-----------------源码开始----------------");
        System.out.println(execute.getSourceCode().trim());
    }

    @Test
    public void testExpressionParse() {
        String text = "a + b + c";
        Expression e1 = CompilerUtil.parseExpression(text);
        System.out.println(e1.getSourceCode());
        assertEquals(text, e1.getSourceCode());

        assertEquals("map.put(\"k\", bb)", CompilerUtil
                .parseExpression("map.put(\"k\", bb)")
                .getSourceCode());

        assertEquals("data.get(0).getId().equals(id)", CompilerUtil
                .parseExpression("data.get(0).getId().equals(id)")
                .getSourceCode());

        assertEquals("m1(p1, p2, m2()) + m3()", CompilerUtil
                .parseExpression("m1(p1, p2, m2()) + m3()")
                .getSourceCode());
    }

    @Override
    public void close() throws IOException {

    }

    /**
     * 测试语法
     * 1.构造器
     * 2.类型强转
     * 3.表达式括号包裹处理
     * */
    @Test
    public void parseTestFile1() {
        TestUtil.compareParsedText(TestFile1.class);
    }

    /**
     * 测试语法
     * 1.成员变量解析
     * 1.修饰符解析解析
     * */
    @Test
    public void parseTestFile2() {
        TestUtil.compareParsedText(TestFile2.class);
    }
}