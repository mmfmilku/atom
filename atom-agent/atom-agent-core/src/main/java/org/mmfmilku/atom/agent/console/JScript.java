package org.mmfmilku.atom.agent.console;

import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.StatementParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.struct.ImportParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Package;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.util.OrdUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JScript {

    private static final ExecuteGoal executor = new ExecuteGoal();

    // 执行方法名约定为execute
    private static final String EXECUTE_METHOD_NAME = "execute";

    public static JScriptResult execute(String code, Object... args) {
        JavaAST javaAST = parseJScript(code);
        return execute(javaAST, args);
    }

    public static JScriptResult execute(List<Import> importList,
                                        List<Statement> statementList, Object... args) {
        JavaAST javaAST = parseJScript(importList, statementList);
        return execute(javaAST, args);
    }

    public static JScriptResult execute(List<Statement> statementList, Object... args) {
        return execute(Collections.emptyList(), statementList, args);
    }

    public synchronized static JScriptResult execute(JavaAST javaAST, Object... args) {
        // 将待执行程序写入执行目标
        OrdUtils.loadOrd(javaAST);
        JScriptResult jScriptResult = new JScriptResult();
        // 执行程序
        try {
            // 保存执行结果
            Object executeReturn = executor.execute(args);
            jScriptResult.setSuccess(true);
            jScriptResult.setExecuteReturn(executeReturn);
        } catch (Exception e) {
            // 保存执行异常
            jScriptResult.setThrowable(e);
            jScriptResult.setSuccess(false);
            e.printStackTrace();
        }
        return jScriptResult;
    }

    public static JavaAST parseJScript(String text) {
        Lexer lexer = new Lexer(text);
        lexer.execute();
        ParserDispatcher dispatcher = new ParserDispatcher(lexer);
        ParserDispatcher.ParserAssembly parserAssembly = dispatcher.newAssembly();
        List<Import> importList = new ArrayList<>();
        List<Statement> statementList = new ArrayList<>();
        parserAssembly.registryList(ImportParser.class, importList::addAll);
        parserAssembly.registryList(StatementParser.class, statementList::addAll);
        parserAssembly.parse();

        return JScript.parseJScript(importList, statementList);
    }

    public static JavaAST parseJScript(List<Import> importList, List<Statement> statementList) {
        java.lang.Class<ExecuteGoal> nativeClass = ExecuteGoal.class;

        JavaAST javaAST = new JavaAST();
        javaAST.setImports(importList);

        Package aPackage = new Package();
        aPackage.setValue(nativeClass.getPackage().getName());
        javaAST.setPackageNode(aPackage);

        Class aClass = new Class(nativeClass.getSimpleName());
        Method method = new Method();
        method.setMethodName(EXECUTE_METHOD_NAME);
        method.setModifier(Modifier.DEFAULT);
        method.setAnnotations(Collections.emptyList());
        method.setMethodParams(Collections.emptyList());
        method.setReturnType(Object.class.getName());
        // TODO 补充方法参数

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
