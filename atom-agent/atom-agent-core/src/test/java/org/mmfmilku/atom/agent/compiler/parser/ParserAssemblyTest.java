package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.CodeBlockParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.StatementParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.struct.ImportParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Import;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

import java.util.stream.Collectors;

public class ParserAssemblyTest {

    /**
     * 正向案例
     * */
    @Test
    public void testAssemblyJScriptParser() {
        String code = " " +
                "import org.junit.Test;" +
                "import org.junit.Gest;" +
                "int a = 1;" +
                "int b = 2;" +
                "int c = a + b;";
        Lexer lexer = new Lexer(code);
        lexer.execute();
        ParserDispatcher dispatcher = new ParserDispatcher(lexer);
        ParserDispatcher.ParserAssembly parserAssembly = dispatcher.newAssembly();
        parserAssembly.registryList(ImportParser.class, importList -> {
            System.out.println("ImportParser parse");
            System.out.println(importList.stream()
                    .map(Import::getSourceCode)
                    .collect(Collectors.joining("\n")));
        });
        parserAssembly.registryList(StatementParser.class, nodeList -> {
            System.out.println("StatementParser parse");
            System.out.println(nodeList.stream()
                    .map(Node::getSourceCode)
                    .collect(Collectors.joining("\n")));
        });
        parserAssembly.parse();
    }

    @Test
    public void testAssemblyJScriptParserUtil() {
        String code = " " +
                "import org.junit.Test;" +
                "import org.junit.Gest;" +
                "int a = 1;" +
                "int b = 2;" +
                "int c = a + b;";
        JavaAST javaAST = CompilerUtil.parseJScript(code);
        System.out.println(javaAST.getSourceCode());
    }

    /**
     * 代码部分缺失
     * */
    @Test
    public void testAssemblyJScriptParserCaseCodeEmpty() {
        String code = " " +
                "import org.junit.Test;" +
                "import org.junit.Gest;";
        Lexer lexer = new Lexer(code);
        lexer.execute();
        ParserDispatcher dispatcher = new ParserDispatcher(lexer);
        ParserDispatcher.ParserAssembly parserAssembly = dispatcher.newAssembly();
        parserAssembly.registryList(ImportParser.class, importList -> {
            System.out.println("ImportParser parse");
            System.out.println(importList);
        });
        parserAssembly.registry(CodeBlockParser.class, codeBlock -> {
            System.out.println("CodeBlockParser parse");
            System.out.println(code);
        });
        parserAssembly.parse();
    }

}
