package org.mmfmilku.atom.agent.console;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.util.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class JTerminalHolderTest {

    private JTerminal jTerminal = JTerminalHolder.newTerminal("test-terminal");

//    @Test
//    public void executeTerminal() {
//        String code = ""
//                + ""
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
        Object parseTerminalCode = ReflectUtils
                .invokeStaticMethod(JTerminalHolder.class, "parseTerminalCode", code);
        List<Statement> statementList = (List<Statement>) parseTerminalCode;
        String enhanceCode = statementList.stream()
                .map(Statement::getSourceCode)
                .collect(Collectors.joining("\n"));
        System.out.println(enhanceCode);
        String expect = "{String a = \"ppp\";\n" +
                "contextVars.put(\"a\", a);\n" +
                "}\n" +
                "{int b = 2;\n" +
                "contextVars.put(\"b\", b);\n" +
                "}\n" +
                "{int c = 9;\n" +
                "contextVars.put(\"c\", c);\n" +
                "}\n" +
                "{int d = b * c;\n" +
                "contextVars.put(\"d\", d);\n" +
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
                ;
        Object parseTerminalCode = ReflectUtils
                .invokeStaticMethod(JTerminalHolder.class, "getJavaAST", code, jTerminal);
        JavaAST javaAST = (JavaAST) parseTerminalCode;
        System.out.println(javaAST.getSourceCode());
        String expect = "package org.mmfmilku.atom.agent.console;\n" +
                "\n" +
                "class JTerminalExecutor {\n" +
                "Object execute() {{String a = \"value of a\";\n" +
                "contextVars.put(\"a\", a);\n" +
                "}\n" +
                "{int b = 4;\n" +
                "contextVars.put(\"b\", b);\n" +
                "}\n" +
                "{int c = 6;\n" +
                "contextVars.put(\"c\", c);\n" +
                "}\n" +
                "{int d = b * c;\n" +
                "contextVars.put(\"d\", d);\n" +
                "}\n" +
                "System.out.println(d);\n" +
                "System.out.println(a.length());\n" +
                "{a = \"new value of a\";\n" +
                "contextVars.put(\"a\", a);\n" +
                "}\n" +
                "}\n" +
                "\n" +
                "}\n";
        assertEquals(expect, javaAST.getSourceCode());
    }
}