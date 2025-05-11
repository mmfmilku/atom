package org.mmfmilku.atom.agent.console;

import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.StatementParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ExpStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ReturnStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarAssignStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;
import org.mmfmilku.atom.agent.util.OrdUtils;
import org.mmfmilku.atom.exception.BizException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * JTerminal操作类，需要负责保证线程安全
 * */
public class JTerminalHolder {

    private static final JTerminalExecutor executor = new JTerminalExecutor();

    // 执行方法名约定为execute
    public static final String EXECUTE_METHOD_NAME = "execute";

    private static AtomicInteger idGen = new AtomicInteger(0);

    private static Map<String, JTerminal> jTerminalMap = new ConcurrentHashMap<>();

    public static JTerminalResult executeTerminal(String id, String code) {
        if (!jTerminalMap.containsKey(id)) {
            throw new BizException("终端不存在:" + id);
        }

        JTerminal jTerminal = jTerminalMap.get(id);
        JavaAST javaAST = getJavaAST(code, jTerminal);

        JTerminalResult jTerminalResult = new JTerminalResult();
        // 锁执行类，同一个执行类，防止不同终端并发执行
        // TODO 每个终端独享一个执行类，细化锁粒度
        synchronized (JTerminalExecutor.class) {
            // 将待执行程序写入执行目标
            OrdUtils.loadOrd(javaAST);
            // 执行程序
            try {
                // 保存执行结果
                Object executeReturn = executor.execute(jTerminal.getContextVars());
                jTerminalResult.setSuccess(true);
                jTerminalResult.setExecuteReturn(executeReturn);
            } catch (Exception e) {
                // 保存执行异常
                jTerminalResult.setThrowable(e);
                jTerminalResult.setSuccess(false);
                e.printStackTrace();
            }
            // 保存执行历史
            jTerminal.getHistory().add(code);
        }

        return jTerminalResult;
    }

    private static JavaAST getJavaAST(String code, JTerminal jTerminal) {
        List<Statement> statementList = parseTerminalCode(code);
        // 处理return语句
        Statement statement = statementList.get(statementList.size() - 1);
        // TODO 代理处理
        if (!(statement instanceof ReturnStatement)) {
            statementList.add(new ReturnStatement(CompilerUtil.parseExpression("\"success\"")));
        }

        JavaAST javaAST = CompilerUtil.newEmptyJavaAST(JTerminalExecutor.class);
        // 使用全局import
        javaAST.setImports(jTerminal.getImportList());
        javaAST.getClassList().get(0)
                .getMethods().get(0)
                .getCodeBlock().setStatements(statementList);
        return javaAST;
    }

    private static List<Statement> parseTerminalCode(String code) {
        Lexer lexer = new Lexer(code);
        lexer.execute();
        ParserDispatcher dispatcher = new ParserDispatcher(lexer);
        ParserDispatcher.ParserAssembly parserAssembly = dispatcher.newAssembly();
        List<Statement> statementList = new ArrayList<>();
        parserAssembly.registryList(StatementParser.class, statementList::addAll, false);
        parserAssembly.parse();
        // TODO 终端语句增强注入
        return statementList.stream()
                .map(JTerminalHolder::enhanceStatement)
                .collect(Collectors.toList());
    }

    private static Statement enhanceStatement(Statement statement) {
        List<Expression> allExpression = statement.getAllExpression();
        for (Expression expression : allExpression) {
            for (Expression baseExp : expression.getBaseExpression()) {
                if (baseExp instanceof Identifier) {
                    // 标识符处理，获取变量从变量上下文中get
                    // TODO 设置代理
                    Identifier identifier = (Identifier) baseExp;
                    identifier.setValue("arg0.get(\"" + identifier.getValue() + "\")");
                }
            }
        }
        // TODO 由于变量上下文map中的value只能存储对象类型，代码中的基础变量需要装箱处理
        if (statement instanceof NestedStatement) {
            // 嵌套语句
            // TODO 获取其中嵌套的语句,例如语句块
            CodeBlock codeBlock = (CodeBlock) statement;
            for (Statement codeBlockStatement : codeBlock.getStatements()) {
                enhanceStatement(codeBlockStatement);
            }
            // TODO
//            codeBlock.setStatements();
            return codeBlock;
        }
        // TODO 变量定义或赋值的操作
        if (statement instanceof VarDefineStatement) {
            VarDefineStatement varDefineStatement = (VarDefineStatement) statement;
            // TODO 语句替换为语句块，并插入保存上下文的语句
            String varName = varDefineStatement.getVarName();
            // 插入语句 arg0.put(varName, ${varName});
            String addExp = String.format("arg0.put(\"%s\", %s);", varName, varName);
            Expression expression = CompilerUtil.parseExpression(addExp);
            CodeBlock codeBlock = new CodeBlock();
            codeBlock.setStatements(Arrays.asList(statement, new ExpStatement(expression)));
            // 原statement替换为codeBlock
            return codeBlock;
            // TODO 变量获取的情况，需要从上下文获取
        }
        if (statement instanceof VarAssignStatement) {
            VarAssignStatement varDefineStatement = (VarAssignStatement) statement;
            // TODO 语句替换为语句块，并插入保存上下文的语句
            String varName = varDefineStatement.getVarName();
            // 插入语句 contextVars.put(varName, ${varName});
            String addExp = String.format("contextVars.put(\"%s\", %s);", varName, varName);
            Expression expression = CompilerUtil.parseExpression(addExp);
            CodeBlock codeBlock = new CodeBlock();
            codeBlock.setStatements(Arrays.asList(statement, new ExpStatement(expression)));
            // 原statement替换为codeBlock
            return codeBlock;
            // TODO 变量获取的情况，需要从上下文获取
        }
        // TODO 其他语句
        return statement;
    }

    public static JTerminal newTerminal(String name) {
        String id = String.valueOf(idGen.addAndGet(1));
        JTerminal jTerminal = new JTerminal(id, name);
        jTerminalMap.put(id, jTerminal);
        return jTerminal;
    }

    public static void deleteTerminal(String id) {
        jTerminalMap.remove(id);
    }

    public static List<String> listId() {
        return jTerminalMap.values()
                .stream()
                .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getId())))
                .map(JTerminal::getId)
                .collect(Collectors.toList());
    }

    public static JTerminal terminalInfo(String id) {
        return jTerminalMap.get(id);
    }

}
