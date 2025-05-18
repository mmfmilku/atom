package org.mmfmilku.atom.agent.compiler.lexer;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.SupportSyntaxSample;
import org.mmfmilku.atom.agent.util.TestUtil;

import java.io.IOException;

public class LexerTest {

    @Test
    public void testLexer() throws IOException {
        String s = TestUtil.getJavaText(SupportSyntaxSample.class);
        Lexer lexer = new Lexer(s);
        lexer.execute();
//        System.out.println(s);
        System.out.println("------------------------");
        System.out.println(lexer.showCode());
        System.out.println("------------------------");
//        System.out.println(lexer.toString());
    }

}