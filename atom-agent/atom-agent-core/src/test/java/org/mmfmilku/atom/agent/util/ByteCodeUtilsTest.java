package org.mmfmilku.atom.agent.util;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.util.FileUtils;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class ByteCodeUtilsTest {

    @Test
    public void testReplaceMethodVar() throws IOException {
        String s = FileUtils.readText(System.getProperty("user.dir") + "\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\compiler\\parser\\ParserTest.java");
        Lexer lexer = new Lexer(s);
        lexer.execute();
        JavaAST javaAST = new ParserDispatcher(lexer).execute();
        Map<String, Map<String, Method>> stringMapMap = ByteCodeUtils.toJavassistCode(javaAST);
        System.out.println(javaAST.getSourceCode());
    }

    @Test
    public void testWriteByteCodeFile() {
        String file = null;
        try {
            file = ByteCodeUtils.writeByteCodeFile("org.mmfmilku.atom.agent.util.TestUtil",
                    System.getProperty("user.dir") + "\\src\\main\\resources\\test");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        System.out.println(file);
    }

}