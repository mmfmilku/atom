package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler
        .CompilerUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler
        .parser.syntax.express.Expression;
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

    public ParserTest() {

    }

    private void m1(String a, int b) {
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

//    @Test
    public void testTmp() {
        String code = "/*\n" +
                " * Decompiled with CFR 0.152.\n" +
                " */\n" +
                "package com.fasterxml.jackson.core;\n" +
                "\n" +
                "import com.fasterxml.jackson.core.JsonGenerator;\n" +
                "import com.fasterxml.jackson.core.JsonLocation;\n" +
                "import com.fasterxml.jackson.core.JsonProcessingException;\n" +
                "\n" +
                "public class JsonGenerationException\n" +
                "extends JsonProcessingException {\n" +
                "\n" +
                "    @Deprecated\n" +
                "    public JsonGenerationException(Throwable rootCause) {\n" +
                "        super(rootCause);\n" +
                "    }\n" +
                "\n" +
                "    @Deprecated\n" +
                "    public JsonGenerationException(String msg) {\n" +
                "        super(msg, (JsonLocation)null);\n" +
                "    }\n" +
                "\n" +
                "    @Deprecated\n" +
                "    public JsonGenerationException(String msg, Throwable rootCause) {\n" +
                "        super(msg, null, rootCause);\n" +
                "    }\n" +
                "\n" +
                "    public JsonGenerationException(Throwable rootCause, JsonGenerator g) {\n" +
                "        super(rootCause);\n" +
                "        this._processor = g;\n" +
                "    }\n" +
                "\n" +
                "    public JsonGenerationException(String msg, JsonGenerator g) {\n" +
                "        super(msg, (JsonLocation)null);\n" +
                "        this._processor = g;\n" +
                "    }\n" +
                "\n" +
                "    public JsonGenerationException(String msg, Throwable rootCause, JsonGenerator g) {\n" +
                "        super(msg, null, rootCause);\n" +
                "        this._processor = g;\n" +
                "    }\n" +
                "\n" +
                "    public JsonGenerationException withGenerator(JsonGenerator g) {\n" +
                "        this._processor = g;\n" +
                "        return this;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public JsonGenerator getProcessor() {\n" +
                "        return this._processor;\n" +
                "    }\n" +
                "}\n";
        JavaAST javaAST = CompilerUtil.parseAST(code);
        System.out.println(javaAST.getSourceCode());
    }
}