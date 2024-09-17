package org.mmfmilku.atom.agent.compiler.parser;
import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.util.FileUtils;
import java.io.IOException;

public class UseImportsTest {@Test()

public void useImportsTest ()throws IOException{String s = FileUtils.readText(System.getProperty("user.dir") + "\\src\\test\\java\\org\\mmfmilku\\atom\\agent\\compiler\\parser\\UseImportsTest.java");
    JavaAST javaAST = CompilerUtil.parseAST(s);
    javaAST.useImport();
    System.out.println(javaAST.getSourceCode());
}

}