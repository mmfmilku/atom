package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.StatementParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.struct.ImportParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Import;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ParserAssemblyTest {

    void testTemplate(String importCodes, String statementCodes, boolean canAbsent) {
        String code = " " +
                importCodes +
                statementCodes;
        Lexer lexer = new Lexer(code);
        lexer.execute();
        ParserDispatcher dispatcher = new ParserDispatcher(lexer);
        ParserDispatcher.ParserAssembly parserAssembly = dispatcher.newAssembly();
        parserAssembly.registryList(ImportParser.class, importList -> {
            String parseCode = importList.stream()
                    .map(Import::getSourceCode)
                    .collect(Collectors.joining("\n"));
            System.out.println("ImportParser parse:");
            System.out.println(parseCode);
            assertEquals(importCodes, parseCode);
        }, canAbsent);
        parserAssembly.registryList(StatementParser.class, statementList -> {
            String parseCode = statementList.stream()
                    .map(Node::getSourceCode)
                    .collect(Collectors.joining("\n"));
            System.out.println("StatementParser parse:");
            System.out.println(parseCode);
            assertEquals(statementCodes, parseCode);
        }, canAbsent);
        parserAssembly.parse();
    }

    /**
     * 正向案例
     * */
    @Test
    public void testAssemblyParser() {
        String importCodes =
                "import org.junit.Test;\n" +
                "import org.junit.Gest;";
        String statementCodes =
                "int a = 1;\n" +
                "int b = 2;\n" +
                "int c = a + b;";
        testTemplate(importCodes, statementCodes, true);
    }

    /**
     * canAbsent为true，代码或import不存在的情况
     * */
    @Test
    public void testAssemblyCaseCanAbsentTrue() {
        String importCodes =
                "";
        String statementCodes =
                "int a = 1;\n" +
                        "int b = 2;\n" +
                        "int c = a + b;";
        // import不存在
        testTemplate(importCodes, statementCodes, true);

        importCodes =
                "import org.junit.Test;\n" +
                        "import org.junit.Gest;";
        statementCodes =
                "";
        // 代码体不存在
        testTemplate(importCodes, statementCodes, true);
    }

    /**
     * canAbsent为false，代码或import不存在的情况
     * */
    @Test
    public void testAssemblyJScriptParserCaseCanAbsentFalse() {
        String importCodes =
                "";
        String statementCodes =
                "int a = 1;\n" +
                        "int b = 2;\n" +
                        "int c = a + b;";
        // import不存在
        try {
            testTemplate(importCodes, statementCodes, false);
            fail("import不能缺少");
        } catch (Exception ignored) {
        }

        // 代码体不存在
        try {
            importCodes =
                    "import org.junit.Test;\n" +
                            "import org.junit.Gest;";
            statementCodes =
                    "";
            testTemplate(importCodes, statementCodes, false);
            fail("语句不能缺少");
        } catch (Exception ignored) {
        }
    }

}
