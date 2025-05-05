package org.mmfmilku.atom.agent.console;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;

import static org.junit.Assert.*;

public class JScriptTest {

    /**
     * 解析正例*/
    @Test
    public void testParseJScript1() {
        String code = " " +
                "import org.junit.Test;" +
                "import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;" +
                "System.out.println(\"ttttt\");" +
                "int a = 133;" +
                "int g = 67;" +
                "int z = a * b + 5;";
        JavaAST javaAST = JScript.parseJScript(code);
        System.out.println("脚本映射源码");
        System.out.println(javaAST.getSourceCode());
        String sourceCodeGen =
                "package org.mmfmilku.atom.agent.console;\n" +
                "import org.junit.Test;\n" +
                "import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;\n" +
                "\n" +
                "class ExecuteGoal {\n" +
                "java.lang.Object execute() {System.out.println(\"ttttt\");\n" +
                "int a = 133;\n" +
                "int g = 67;\n" +
                "int z = a * b + 5;\n" +
                "}\n" +
                "\n" +
                "}\n";
        assertEquals(sourceCodeGen, javaAST.getSourceCode());
    }
}