package org.mmfmilku.atom.agent.compiler.parser;
import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.util.FileUtils;
import javax.annotation.Resource;
import java.io.IOException;
import static org.junit.Assert.*;
@SuppressWarnings("f")
@Resource()

public class ParserSelfCheckTest {
public void testNo (){}

@Test()

public void selfCheck1 ()throws IOException{String s = FileUtils.readText("F:\\dev\\project\\atom\\atom-agent\\atom-agent-core\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\compiler\\parser\\ParserSelfCheckTest.java");
Lexer lexer = new Lexer(s);
lexer.execute();
Parser parser = new Parser(lexer);
Node execute = new Parser(lexer).execute();
System.out.println(execute);
int a = 1;
int b = 3;
a++;
--b;
int c = a++ + b;
System.out.println(a + b - c);
if (c > 0){System.out.println("c大于0");
}
{}
System.out.println(ParserSelfCheckTest.class.getName());
System.out.println("-----------------源码开始----------------");
System.out.println(execute.getSourceCode());
assertEquals(s.trim(), execute.getSourceCode().trim().concat("").trim().trim().concat(""));
}

}