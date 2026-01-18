package org.mmfmilku.atom.agent.console;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.class_for_input_test.InputForGetJavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.util.ByteCodeUtils;
import org.mmfmilku.atom.agent.util.TestUtil;
import org.mmfmilku.atom.util.ReflectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class JTerminalTest {

    private static JTerminalDomain jTerminalDomain = JTerminal.newTerminal("test-terminal");

    @Test
    public void testNewTerminal() {
        int size = JTerminal.listId().size();
        JTerminalDomain test1 = JTerminal.newTerminal("test-1");
        assertEquals(size + 1, JTerminal.listId().size());
        JTerminal.deleteTerminal(test1.getId());
        assertEquals(size, JTerminal.listId().size());
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
        JTerminalDomain jTerminalDomain = JTerminal.newTerminal("parseTerminalCode");
        Object parseTerminalCode = ReflectUtils
                .invokeStaticMethod(JTerminal.class, "parseTerminalCode", code, jTerminalDomain);
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
        String testClassFullName = InputForGetJavaAST .class.getName();
        String javaText = TestUtil.getJavaText(InputForGetJavaAST .class);
        JavaAST classAst = CompilerUtil.parseAST(javaText);
        Method method = classAst.getClassList().get(0).getMethods().get(0);
        String code = method.getCodeBlock().getStatementSource();
        System.out.println("-------------test1 JTerminalTest.getJavaAST,input:\n" + code);
        Object parseTerminalCode = ReflectUtils
                .invokeStaticMethod(JTerminal.class, "getJavaAST", code, jTerminalDomain);
        JavaAST javaAST = (JavaAST) parseTerminalCode;
        System.out.println(javaAST.getSourceCode());

        Method method2 = classAst.getClassList().get(0).getMethods().get(1);
        String code2 = method2.getCodeBlock().getStatementSource();
        System.out.println("-------------test2 JTerminalTest.getJavaAST,input:\n" + code2);
        Object parseTerminalCode2 = ReflectUtils
                .invokeStaticMethod(JTerminal.class, "getJavaAST", code, jTerminalDomain);
        JavaAST javaAST2 = (JavaAST) parseTerminalCode2;
        System.out.println(javaAST2.getSourceCode());
    }

}