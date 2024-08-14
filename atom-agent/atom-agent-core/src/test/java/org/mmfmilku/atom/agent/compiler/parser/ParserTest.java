package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.util.FileUtils;

import javax.annotation.Resource;
import java.io.IOException;

//import static org.junit.Assert.*;

@SuppressWarnings("f")
@Resource
public class ParserTest {

    @Test
    public void testParser() throws IOException {
        String s = FileUtils.readText("F:\\dev\\project\\atom\\atom-agent\\atom-agent-core\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\compiler\\parser\\ParserTest.java");
        Lexer lexer = new Lexer(s);
        lexer.execute();
        Parser parser = new Parser(lexer);
        parser.execute();
        System.out.println(parser);
    }

}