package org.mmfmilku.atom.agent.console;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.util.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class JTerminalHolderTest {

    private static JTerminal jTerminal = JTerminalHolder.newTerminal("test-terminal");

    @Test
    public void testNewTerminal() {
        int size = JTerminalHolder.listId().size();
        JTerminal test1 = JTerminalHolder.newTerminal("test-1");
        assertEquals(size + 1, JTerminalHolder.listId().size());
        JTerminalHolder.deleteTerminal(test1.getId());
        assertEquals(size, JTerminalHolder.listId().size());
    }

//    @Test
//    public void executeTerminal() {
//        String code = ""
//                + "a = \"\";"
//                ;
//        JTerminalHolder.executeTerminal(jTerminal.getId(), code);
//    }

    @Test
    public void parseTerminalCode() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String code = ""
                + "String a = \"ppp\";"
                + "int b = 2;"
                + "int c = 9;"
                + "int d = b * c;"
                ;
        JTerminal jTerminal = JTerminalHolder.newTerminal("parseTerminalCode");
        Object parseTerminalCode = ReflectUtils
                .invokeStaticMethod(JTerminalHolder.class, "parseTerminalCode", code, jTerminal);
        List<Statement> statementList = (List<Statement>) parseTerminalCode;
        String enhanceCode = statementList.stream()
                .map(Statement::getSourceCode)
                .collect(Collectors.joining("\n"));
        System.out.println(enhanceCode);
        String expect = "{String a = \"ppp\";\n" +
                "$1.put(\"a\", a);\n" +
                "}\n" +
                "{int b = 2;\n" +
                "$1.put(\"b\", b);\n" +
                "}\n" +
                "{int c = 9;\n" +
                "$1.put(\"c\", c);\n" +
                "}\n" +
                "{int d = $1.get(\"b\") * $1.get(\"c\");\n" +
                "$1.put(\"d\", d);\n" +
                "}";
        assertEquals(expect, enhanceCode);
    }

    @Test
    public void getJavaAST() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String code = ""
                + "String a = \"value of a\";"
                + "int b = 4;"
                + "int c = 6;"
                + "int d = b * c;"
                + "System.out.println(d);"
                + "System.out.println(a.length());"
                + "a = \"new value of a\";"
                + "d = c = b = a.length();"
                ;
        Object parseTerminalCode = ReflectUtils
                .invokeStaticMethod(JTerminalHolder.class, "getJavaAST", code, jTerminal);
        JavaAST javaAST = (JavaAST) parseTerminalCode;
        ByteCodeUtils.toJavassistCode(javaAST);
        System.out.println(javaAST.getSourceCode());
        String expect = "package org.mmfmilku.atom.agent.console;\n" +
                "\n" +
                "class JTerminalExecutor {\n" +
                "Object execute(java.util.Map arg0) {{String a = \"value of a\";\n" +
                "$1.put(\"a\", a);\n" +
                "}\n" +
                "{int b = 4;\n" +
                "$1.put(\"b\", b);\n" +
                "}\n" +
                "{int c = 6;\n" +
                "$1.put(\"c\", c);\n" +
                "}\n" +
                "{int d = $1.get(\"b\") * $1.get(\"c\");\n" +
                "$1.put(\"d\", d);\n" +
                "}\n" +
                "System.out.println($1.get(\"d\"));\n" +
                "System.out.println($1.get(\"a\").length());\n" +
                "{a = \"new value of a\";\n" +
                "$1.put(\"a\", a);\n" +
                "}\n" +
                "{d = c = b = $1.get(\"a\").length();\n" +
                "$1.put(\"d\", d);\n" +
                "$1.put(\"c\", c);\n" +
                "$1.put(\"b\", b);\n" +
                "}\n" +
                "return \"success\";\n" +
                "}\n" +
                "\n" +
                "}\n";
        assertEquals(expect, javaAST.getSourceCode());
    }
}