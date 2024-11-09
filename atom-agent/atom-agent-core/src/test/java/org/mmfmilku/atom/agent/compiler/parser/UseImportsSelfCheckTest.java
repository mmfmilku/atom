package org.mmfmilku.atom.agent.compiler.parser;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.assertEquals;

public class UseImportsSelfCheckTest {@Test()

public void useImportsTest()throws IOException {String s = org.mmfmilku.atom.agent.util.TestUtil.getJavaText(this.getClass());
org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST(s);
javaAST.useImport();
System.out.println(javaAST.getSourceCode());
org.junit.Assert.assertEquals(s, javaAST.getSourceCode());
}


public org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST importExample1(org.mmfmilku.atom.agent.compiler.parser.syntax.Method m1, java.util.List list) {org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST("");
org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST2 = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST("");
long count = java.util.stream.Stream.concat(java.util.stream.Stream.of(1, 2), java.util.stream.Stream.of(3, 4, 5)).count();
org.junit.Assert.assertEquals(count, 5);
return null;
}


public org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST importExample2(org.mmfmilku.atom.agent.compiler.parser.syntax.Method m1, java.util.List list) {org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST("");
org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST2 = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST("");
long count = java.util.stream.Stream.concat(java.util.stream.Stream.of(1, 2), java.util.stream.Stream.of(3, 4, 5)).count();
org.junit.Assert.assertEquals(count, 5);
return null;
}

}