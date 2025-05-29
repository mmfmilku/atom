package org.mmfmilku.atom.agent.console;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.agent.util.OrdUtils;

import java.util.Map;

import static org.junit.Assert.*;

public class JScriptTest {

    /**
     * 解析正例
     * */
    @Test
    public void testParseJScript1() {
        String code = " " +
                "System.out.println(\"ttttt\");\n" +
                "int a = 133;" +
                "return a;";
        JavaAST javaAST = JScript.parseJScript(code);
        System.out.println("脚本映射源码");
        System.out.println(javaAST.getSourceCode());
        String sourceCodeGen =
                "package org.mmfmilku.atom.agent.console;\n" +
                "\n" +
                "class JScriptExecutor {\n" +
                "Object execute() {System.out.println(\"ttttt\");\n" +
                "int a = 133;\n" +
                "return a;\n" +
                "}\n" +
                "\n" +
                "}\n";
        assertEquals(sourceCodeGen, javaAST.getSourceCode());
    }

    /**
     * 解析正例，解析补充return语句
     * */
    @Test
    public void testParseJScript2() {
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
                        "class JScriptExecutor {\n" +
                        "Object execute() {System.out.println(\"ttttt\");\n" +
                        "int a = 133;\n" +
                        "int g = 67;\n" +
                        "int z = a * b + 5;\n" +
                        "return \"success\";\n" +
                        "}\n" +
                        "\n" +
                        "}\n";
        assertEquals(sourceCodeGen, javaAST.getSourceCode());
    }

    @Test
    public void testParseJScript4() {
        String code = " " +
                "import com.example.bootstudy.service.TestService;" +
                "System.out.println(\"jjjjjjjjjjjjjj!!!!\");\n" +
                "        TestService test = new TestService();\n" +
                "        System.out.println(test.add(5, 8));\n" +
                "        System.out.println(TestService.class);\n" +
                "        System.out.println(\"jjjjjjjjjjjjjjjjjj end!!!!\");"
                ;
        JavaAST javaAST = JScript.parseJScript(code);
        System.out.println("脚本映射源码");
        ByteCodeUtils.toJavassistCode(javaAST);
        System.out.println(javaAST.getSourceCode());
    }

    /**
     * 测试语法树对ord的转换
     * */
    @Test
    public void testParseJScriptAst2Ord() {

        String code = "System.out.println(\"jjjjjjjjjjjjjj!!!!\");\n" +
                "        TestService test = new TestService();\n" +
                "        System.out.println(test.add(5, 8));\n" +
                "        System.out.println(\"jjjjjjjjjjjjjjjjjj end!!!!\");\n";
        JavaAST javaAST = JScript.parseJScript(code);
        Map<String, ClassORDDefine> defineMap = OrdUtils.astToOrd(javaAST);
        System.out.println(defineMap);
        assertTrue(defineMap.containsKey(JScriptExecutor.class.getName()));

    }
}