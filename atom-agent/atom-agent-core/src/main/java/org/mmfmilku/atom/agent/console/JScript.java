package org.mmfmilku.atom.agent.console;

import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.StatementParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.struct.ImportParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ReturnStatement;
import org.mmfmilku.atom.agent.util.OrdUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JScript {

    private static final JScriptExecutor executor = new JScriptExecutor();

    // 执行方法名约定为execute
    public static final String EXECUTE_METHOD_NAME = "execute";

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
        System.out.println("execute jScript ast:");
        System.out.println(javaAST.getSourceCode());
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

        return parseJScript(importList, statementList);
    }

    public static JavaAST parseJScript(List<Import> importList, List<Statement> statementList) {
        // 处理return语句
        Statement statement = statementList.get(statementList.size() - 1);
        // TODO 代理处理
        if (!(statement instanceof ReturnStatement)) {
            statementList.add(new ReturnStatement(CompilerUtil.parseExpression("\"success\"")));
        }
        JavaAST javaAST = CompilerUtil.newEmptyJavaAST(JScriptExecutor.class);
        javaAST.setImports(importList);
        javaAST.getClassList().get(0)
                .getMethods().get(0)
                .getCodeBlock().setStatements(statementList);

        return javaAST;
    }

}
