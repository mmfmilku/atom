package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.util.TestUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class UseImportsTest {

    @Test
    public void useImportsTest() {
        String s = TestUtil.getJavaText(getClass());
        JavaAST javaAST = CompilerUtil.parseAST(s);
        javaAST.useImport();
        System.out.println(javaAST.getSourceCode());
        String a = javaAST.getClassList().get(1).getMethods().get(0).getSourceCode();
        String b = javaAST.getClassList().get(2).getMethods().get(0).getSourceCode();
        assertEquals(a, b);
    }
}

class Example1 {
    public Map importExample(Method m1, List list) {
        System.out.println(List.class);
        JavaAST javaAST = CompilerUtil.parseAST("");
        JavaAST javaAST2 = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST("");
        long count = Stream.concat(java.util.stream.Stream.of(1, 2), Stream.of(3, 4, 5)).count();
        assertEquals(count, 5);
        return Collections.EMPTY_MAP;
    }
}

class Example2 {
    public java.util.Map importExample(Method m1, java.util.List list) {
        System.out.println(java.util.List.class);
        org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST = CompilerUtil.parseAST("");
        org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST javaAST2 = org.mmfmilku.atom.agent.compiler.CompilerUtil.parseAST("");
        long count = Stream.concat(Stream.of(1, 2), Stream.of(3, 4, 5)).count();
        assertEquals(count, 5);
        return java.util.Collections.EMPTY_MAP;
    }
}