package org.mmfmilku.atom.agent.util;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.Parser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;

import java.io.IOException;
import java.util.Collection;

public class ByteCodeUtilsTest {

    @Test
    public void testReplaceMethodVar() throws IOException {
        String s = FileUtils.readText(System.getProperty("user.dir") + "\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\compiler\\parser\\ParserTest.java");
        Lexer lexer = new Lexer(s);
        lexer.execute();
        JavaAST javaAST = new Parser(lexer).execute();
        javaAST.getClassList()
                .stream()
                .map(Class::getMethods)
                .flatMap(Collection::stream)
                .forEach(ByteCodeUtils::replaceMethodVar);
    }

}