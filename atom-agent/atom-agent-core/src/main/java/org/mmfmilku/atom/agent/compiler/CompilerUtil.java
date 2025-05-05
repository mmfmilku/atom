package org.mmfmilku.atom.agent.compiler;

import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.StatementParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.struct.ImportParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Package;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CompilerUtil {

    public static JavaAST parseAST(String code) {
        Lexer lexer = new Lexer(code);
        lexer.execute();
        ParserDispatcher parser = new ParserDispatcher(lexer);
        return parser.execute();
    }

    public static Expression parseExpression(String text) {
        Lexer lexer = new Lexer(text);
        lexer.execute();
        return new ParserDispatcher(lexer).getExpression();
    }

    public static JavaAST parseJScript(String text) {
        Lexer lexer = new Lexer(text);
        lexer.execute();
        ParserDispatcher dispatcher = new ParserDispatcher(lexer);
        ParserDispatcher.ParserAssembly parserAssembly = dispatcher.newAssembly();
        List<Import> importList = new ArrayList<>();
        List<Statement> statementList = new ArrayList<>();
        parserAssembly.registryList(ImportParser.class, nodeList -> {
            importList.addAll(nodeList);
        });
        parserAssembly.registryList(StatementParser.class, nodeList -> {
            statementList.addAll(nodeList);
        });
        parserAssembly.parse();
        JavaAST javaAST = new JavaAST();
        javaAST.setImports(importList);

        Package aPackage = new Package();
        aPackage.setValue("org.mmfmilku.test");
        javaAST.setPackageNode(aPackage);

        Class aClass = new Class("TODO");
        Method method = new Method();
        method.setMethodName("TODO");
        method.setModifier(Modifier.DEFAULT);
        method.setAnnotations(Collections.emptyList());
        method.setMethodParams(Collections.emptyList());

        CodeBlock codeBlock = new CodeBlock();
        codeBlock.setStatements(statementList);
        method.setCodeBlock(codeBlock);

        aClass.setMethods(Arrays.asList(method));
        aClass.setModifier(Modifier.DEFAULT);
        aClass.setAnnotations(Collections.emptyList());
        aClass.setMembers(Collections.emptyList());
        aClass.setConstructors(Collections.emptyList());

        javaAST.setClassList(Arrays.asList(aClass));

        return javaAST;
    }

}
