package org.mmfmilku.atom.agent.console;

import org.mmfmilku.atom.agent.compiler.CompilerUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.StatementParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.struct.ImportParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Import;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.AccessPrivilege;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.StringLiteral;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ExpStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ReturnStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.VarAssign;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;
import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.agent.util.OrdUtils;
import org.mmfmilku.atom.exception.BizException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * JTerminal操作类，需要负责保证线程安全
 * */
public class JTerminal {

    private static final JTerminalExecutor executor = new JTerminalExecutor();

    // 执行方法名约定为execute
    public static final String EXECUTE_METHOD_NAME = "execute";

    private static AtomicInteger idGen = new AtomicInteger(0);

    private static Map<String, JTerminalDomain> jTerminalMap = new ConcurrentHashMap<>();

    public static JTerminalResult executeTerminal(String id, String code) {
        if (!jTerminalMap.containsKey(id)) {
            throw new BizException("终端不存在:" + id);
        }
        System.out.println("executeTerminal: " + code);

        JTerminalDomain jTerminalDomain = jTerminalMap.get(id);
        jTerminalDomain.getCurrVars().clear();
        JavaAST javaAST = getJavaAST(code, jTerminalDomain);

        JTerminalResult jTerminalResult = new JTerminalResult();
        // 锁执行类，同一个执行类，防止不同终端并发执行
        // TODO 每个终端独享一个执行类，细化锁粒度
        synchronized (JTerminalExecutor.class) {
            // 将待执行程序写入执行目标
            if (AgentProperties.byteCodeCompile()) {
                OrdUtils.loadOrd(javaAST);
            } else {
                String sourceCode = javaAST.getSourceCode();
                String className = javaAST.getClassList().get(0).getClassFullName();
                OrdUtils.loadOrd(className, sourceCode);
            }

            // 执行程序
            try {
                // 保存执行结果
                Object executeReturn = executor.execute(jTerminalDomain.getContextVars(), jTerminalDomain.getContextVarsType());
                jTerminalResult.setSuccess(true);
                jTerminalResult.setExecuteReturn(executeReturn);
                // 执行成功，添加新增的import
                jTerminalDomain.getImportList().addAll(javaAST.getImports());
            } catch (Exception e) {
                // 保存执行异常
                jTerminalResult.setThrowable(e);
                jTerminalResult.setSuccess(false);
                e.printStackTrace();
            }
            // 保存执行历史
            jTerminalDomain.getHistory().add(code);
            // 保存结果历史
            jTerminalDomain.getResultHistory().add(jTerminalResult);
        }

        return jTerminalResult;
    }

    private static JavaAST getJavaAST(String code, JTerminalDomain jTerminalDomain) {
        List<Statement> statementList = parseTerminalCode(code, jTerminalDomain);
        if (statementList.isEmpty()) {
            statementList.add(new ReturnStatement(new StringLiteral("\"\"")));
        } else {
            // 处理return语句
            Statement statement = statementList.get(statementList.size() - 1);
            // 最后一条语句非return语句
            if (!(statement instanceof ReturnStatement)) {
                if (statementList.size() == 1) {
                    statementList.add(new ReturnStatement(new StringLiteral("\"\"")));
                } else {
                    statementList.add(new ReturnStatement(new StringLiteral("\"\"")));
                }
            }
        }

        JavaAST javaAST = CompilerUtil.newEmptyJavaAST(JTerminalExecutor.class);
        // 使用全局import
        javaAST.setImports(new ArrayList<>(jTerminalDomain.getImportList()));
        javaAST.getClassList().get(0)
                .getMethods().get(0)
                .getCodeBlock().setStatements(statementList);

        // 构造echo方法
        Method echoMethod = new Method();
        Modifier modifier = new Modifier();
        modifier.setAccessPrivilege(AccessPrivilege.PRIVATE);
        echoMethod.setModifier(modifier);
        echoMethod.setMethodName("echo");
        VarDefineStatement varDefine = new VarDefineStatement("Object", "arg0");
        echoMethod.setMethodParams(Collections.singletonList(varDefine));
        echoMethod.setReturnType("void");
        echoMethod.setAnnotations(Collections.emptyList());
        CodeBlock codeBlock = new CodeBlock();
        Expression expression = CompilerUtil.parseExpression(
                "org.mmfmilku.atom.agent.log.ScreenLogger.print(org.mmfmilku.atom.agent.util.AgentExeUtils.toString(arg0))");
        codeBlock.setStatements(Collections.singletonList(new ExpStatement(expression)));
        echoMethod.setCodeBlock(codeBlock);

        javaAST.getClassList().get(0).getMethods().add(echoMethod);
        javaAST.buildLinkedNode();
        return javaAST;
    }

    private static List<Statement> parseTerminalCode(String code, JTerminalDomain jTerminalDomain) {
        Lexer lexer = new Lexer(code);
        lexer.execute();
        ParserDispatcher dispatcher = new ParserDispatcher(lexer);
        ParserDispatcher.ParserAssembly parserAssembly = dispatcher.newAssembly();

        List<Import> importList = new ArrayList<>();
        List<Statement> statementList = new ArrayList<>();
        parserAssembly.registryList(ImportParser.class, importList::addAll, true);
        parserAssembly.registryList(StatementParser.class, statementList::addAll, true);
        parserAssembly.parse();

        // 添加import，此处添加import语句，会导致即使语句执行失败也会添加
        jTerminalDomain.getImportList().addAll(importList);

        // TODO 终端语句增强注入
        return statementList.stream()
                .map(statement -> enhanceStatement(statement, jTerminalDomain))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<Statement> enhanceStatement(Statement statement, JTerminalDomain jTerminalDomain) {
        enhanceIdentifier(statement, jTerminalDomain);
        return enhanceLeafStatement(statement, jTerminalDomain);
    }

    private static List<Statement> enhanceLeafStatement(Statement statement, JTerminalDomain jTerminalDomain) {
        if (statement instanceof NestedStatement) {
            // 嵌套语句
            // TODO 获取其中嵌套的语句,例如语句块
            NestedStatement nestedStatement = (NestedStatement) statement;
            return nestedStatement.getNested().stream()
                    .map(stmt -> enhanceLeafStatement(stmt, jTerminalDomain))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
        // 变量定义赋值的增强
        if (statement instanceof VarDefineStatement) {
            VarDefineStatement varDefineStatement = (VarDefineStatement) statement;
            // 语句替换为语句块，并插入保存上下文的语句
            String varName = varDefineStatement.getVarName();
            String varType = varDefineStatement.getVarType();
            jTerminalDomain.getCurrVars().add(varName);

            // 保存变量，插入语句 arg0.put("varName", ${varName});
            String varExpStr = String.format("arg0.put(\"%s\", %s);", varName, varName);
            Expression varExp = CompilerUtil.parseExpression(varExpStr);

            // 保存变量类型，插入语句 arg1.put("varName", "${varType}");
            String typeExpStr = String.format("arg1.put(\"%s\", \"%s\");", varName, varType);
            Expression typeExp = CompilerUtil.parseExpression(typeExpStr);

            // 返回增强后的语句列表
            return Arrays.asList(statement,
                    new ExpStatement(varExp), new ExpStatement(typeExp));
        }
        // 变量仅赋值的增强
        if (statement instanceof ExpStatement) {
            ExpStatement expStatement = (ExpStatement) statement;
            Expression exp = expStatement.getExpression();
            if (exp instanceof VarAssign) {
                List<Statement> statements = new ArrayList<>();
                CodeBlock codeBlock = new CodeBlock();
                codeBlock.setStatements(statements);
                // 循环处理赋值表达式，处理多变量赋值的情况如：  a = b = c = arr.length();
                // 待赋值的变量   如：a,b,c
                List<String> toAssignVars = new ArrayList<>();
                do {
                    VarAssign varAssign = (VarAssign) exp;
                    toAssignVars.add(varAssign.getVarName());
                    exp = varAssign.getAssignExpression();
                } while (exp instanceof VarAssign);

                // 此时的exp为最终赋值的表达式 如：arr.length()
                // 处理所有待赋值的变量
                for (String varName : toAssignVars) {
                    // 插入语句 arg0.put("varName", assignExpression);
                    String addExp = String.format("arg0.put(\"%s\", %s);", varName, exp.getSourceCode());
                    Expression expression = CompilerUtil.parseExpression(addExp);
                    statements.add(new ExpStatement(expression));
                }
                return statements;
            }
            // TODO 变量获取的情况，需要从上下文获取
        }
        // TODO 其他语句
        return Collections.singletonList(statement);
    }

    private static void enhanceIdentifier(Statement statement, JTerminalDomain jTerminalDomain) {
        List<Expression> allExpression = statement.getAllExpression();
        for (Expression expression : allExpression) {
            // 变量赋值语句的处理
            for (Expression baseExp : expression.getLeafExpression()) {
                if (baseExp instanceof Identifier) {
                    // 标识符处理，获取变量从变量上下文中get
                    // TODO 遗漏情况，同import，考虑一起处理
                    // 1.变量名等于类名的情况，会误替换
                    // 2.对于调用链，只有首个标识符需要替换
                    // 变量上下文中存在的变量为历史变量，与本次添加的变量一起判断
                    Identifier identifier = (Identifier) baseExp;
                    String value = identifier.getValue();
                    // 存在历史上下文，且非本次新增的变量
                    if (jTerminalDomain.getContextVars().containsKey(value)) {
                        Map<String, String> contextVarsType = jTerminalDomain.getContextVarsType();
                        String varType = contextVarsType.getOrDefault(value, "Object");
                        // 由于变量上下文map中的value只能存储对象类型，获取变量时需要强转处理
                        // 处理类型强转 var -> (varType) arg0.get("var")
                        identifier.setValue(String.format("((%s) arg0.get(\"%s\"))", varType, value));
                        System.out.println("JTerminal增强 " + value + "=>" + identifier.getValue());
                    }
                }
            }
        }
    }

    public static JTerminalDomain newTerminal(String name) {
        String id = String.valueOf(idGen.addAndGet(1));
        JTerminalDomain jTerminalDomain = new JTerminalDomain(id, name);
        jTerminalMap.put(id, jTerminalDomain);
        return jTerminalDomain;
    }

    public static void deleteTerminal(String id) {
        jTerminalMap.remove(id);
    }

    public static List<String> listId() {
        return jTerminalMap.values()
                .stream()
                .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getId())))
                .map(JTerminalDomain::getId)
                .collect(Collectors.toList());
    }

    public static JTerminalDomain terminalInfo(String id) {
        return jTerminalMap.get(id);
    }

    public static void clear() {
        jTerminalMap.clear();
    }
}
