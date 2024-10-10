package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;
import org.mmfmilku.atom.agent.util.FileUtils;

import javax.annotation.Resource;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("f")
@Resource()

public class ParserSelfCheckTest {
public void testNo (){}

@Test()

public void selfCheck1 ()throws IOException{String s = FileUtils.readText(System.getProperty("user.dir") + "\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\compiler\\parser\\ParserSelfCheckTest.java");
String s1 = "ss".concat("3") + "ff".trim();
Node execute = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST(s);
System.out.println(execute);
int a = 1;
int b = 3;
a++;
--b;
int c = a++ + b;
int d = new Integer(3) + 4;
System.out.println(a + b - c);
if (c > 0){System.out.println("c大于0");
}
{}
System.out.println(ParserSelfCheckTest.class.getName());
System.out.println("-----------------源码开始----------------");
System.out.println(execute.getSourceCode());
assertEquals(s.trim(), execute.getSourceCode().trim().concat("").trim().trim().concat(""));
}


public String ifTestCase (Integer integer){int a = 1;
int b = 2;
int c = 3;
if (c > 0){System.out.println("c大于0");
new Integer(3).byteValue();
new String().trim();
}
if (c != 0){if (c == 0){if (c <= 0){System.out.println("c大于0");
}
}
}
if (1 == 1){if (2 >= 1){if (3 != 2){return new String("ooo");
}
}else if (a + b > 1){if (1 + 3 >= 2){}
}else {System.out.println(3);
}
}
if (a != 1){return "2!=3";
}
if (1 == 1){return "1";
}
return String.valueOf(integer);
}

}