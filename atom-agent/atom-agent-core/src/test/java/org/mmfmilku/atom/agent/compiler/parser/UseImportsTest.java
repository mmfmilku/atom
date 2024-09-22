package org.mmfmilku.atom.agent.compiler.parser;
import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.util.FileUtils;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.Assert.*;

public class UseImportsTest {@Test()

public void useImportsTest ()throws IOException{String s = org.mmfmilku.atom.agent.util.FileUtils.readText(System.getProperty("user.dir") + "\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\compiler\\parser\\UseImportsTest.java");
JavaAST javaAST = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST(s);
javaAST.useImport();
System.out.println(javaAST.getSourceCode());
//assertEquals(s, javaAST.getSourceCode());
}

public JavaAST importExample1(Method m1, List list) {
JavaAST javaAST = CompilerUtil.parseAST("");
JavaAST javaAST2 = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST("");
long count = Stream.concat(java.util.stream.Stream.of(1, 2), Stream.of(3, 4, 5)).count();
assertEquals(count, 5);
return null;
}

public org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST importExample2(Method m1, java.util.List list) { org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST = CompilerUtil.parseAST("");
org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST2 = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST("");
long count = Stream.concat(Stream.of(1, 2), Stream.of(3, 4, 5)).count();
assertEquals(count, 5);
return null;
}

}