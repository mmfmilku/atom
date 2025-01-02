package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.util.TestUtil;

public class QuickTest {

    @Test
    public void quickTest() {
        String javaText = TestUtil.getJavaText(QuickCode.class);
        JavaAST javaAST = CompilerUtil.parseAST(javaText);
        javaAST.useImport();
        System.out.println(javaAST.getSourceCode());
    }

}


