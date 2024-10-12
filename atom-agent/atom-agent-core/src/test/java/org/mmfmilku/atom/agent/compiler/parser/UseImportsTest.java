package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.util.TestUtil;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class UseImportsTest {

    @Test
    public void useImportsTest() {
        String s = TestUtil.getJavaText(getClass());
        JavaAST javaAST = CompilerUtil.parseAST(s);
        javaAST.useImport();
        System.out.println(javaAST.getSourceCode());
        List classList = javaAST.getClassList();
        String a = "a";
        String b = "b";
        Class aClass = javaAST.getClassList().get(0);
        if (javaAST.getClassList().get(1).getClassName().equals("Example1")) {
            a = aClass.getMethods().get(0).getSourceCode();
        }
        if (javaAST.getClassList().get(2).getClassName().equals("Example2")) {
            b = aClass.getMethods().get(0).getSourceCode();
        }
        assertEquals(a, b);
    }
}

class Example1 {
    public JavaAST importExample(Method m1, List list) {
        JavaAST javaAST = CompilerUtil.parseAST("");
        JavaAST javaAST2 = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST("");
        long count = Stream.concat(java.util.stream.Stream.of(1, 2), Stream.of(3, 4, 5)).count();
        assertEquals(count, 5);
        return null;
    }
}

class Example2 {
    public org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST importExample2(Method m1, java.util.List list) {
        org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST = CompilerUtil.parseAST("");
        org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST2 = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST("");
        long count = Stream.concat(Stream.of(1, 2), Stream.of(3, 4, 5)).count();
        assertEquals(count, 5);
        return null;
    }
}