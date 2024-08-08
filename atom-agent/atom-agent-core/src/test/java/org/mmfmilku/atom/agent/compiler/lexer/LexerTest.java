package org.mmfmilku.atom.agent.compiler.lexer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;
import org.mmfmilku.atom.agent.util.FileUtils;

import java.io.IOException;

import static org.junit.Assert.*;

public class LexerTest {

    @Test
    public void testLexer() throws IOException {
        String s = FileUtils.readText("F:\\dev\\project\\atom\\atom-agent\\atom-agent-core\\src\\main\\java\\org\\mmfmilku\\atom\\agent\\config\\ORDParser.java");
        Lexer lexer = new Lexer(s);
        lexer.execute();
//        System.out.println(s);
        System.out.println("------------------------");
//        System.out.println(lexer.showCode());
        System.out.println("------------------------");
        System.out.println(lexer.toString());
    }

}