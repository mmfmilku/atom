package org.mmfmilku.atom.agent.compiler.parser;
import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.util.FileUtils;
import java.io.IOException;
import java.util.stream.Stream;
import static org.junit.Assert.*;

public class UseImportsTest {@Test()

public void useImportsTest ()throws IOException{String s = org.mmfmilku.atom.agent.util.FileUtils.readText(System.getProperty("user.dir") + "\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\compiler\\parser\\UseImportsTest.java");
org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST(s);
javaAST.useImport();
long count = java.util.stream.Stream.concat(java.util.stream.Stream.of(1, 2), java.util.stream.Stream.of(3, 4, 5)).count();
assertEquals(count, 5);
System.out.println(javaAST.getSourceCode());
assertEquals(s, javaAST.getSourceCode());
}

}