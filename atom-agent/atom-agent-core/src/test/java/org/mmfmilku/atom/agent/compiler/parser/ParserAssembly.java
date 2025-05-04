package org.mmfmilku.atom.agent.compiler.parser;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.CodeBlockParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.struct.ImportParser;

public class ParserAssembly {

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
            System.out.println(importList);
        });
        parserAssembly.registry(CodeBlockParser.class, codeBlock -> {
            System.out.println("CodeBlockParser parse");
            System.out.println(code);
        });
        parserAssembly.parse();
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
