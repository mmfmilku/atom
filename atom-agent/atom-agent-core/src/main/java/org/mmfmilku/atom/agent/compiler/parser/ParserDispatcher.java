package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Package;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.AccessPrivilege;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.*;
import org.mmfmilku.atom.agent.compiler.parser.handle.ParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.handle.TryParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.VarDefineAssignParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.VarDefineParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.*;
import org.mmfmilku.atom.exception.SystemException;
import org.mmfmilku.atom.util.AssertUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Parser
 *
 * @author chenxp
 * @date 2024/8/8:16:34
 */
public class ParserDispatcher {

    private static Statement EMPTY = new CodeBlock();

    private Lexer lexer;

    public ParserDispatcher(Lexer lexer) {
        this.lexer = lexer;
    }

    public JavaAST execute() {
        ParserHelper handle = new ParserHelper();
        return handle.parse(null);
    }

    public Expression getExpression() {
        ParserHelper handle = new ParserHelper();
        return handle.getExpression();
    }

    private class ParserHelper implements ParserHandle {
        List<Token> tokens;
        int curr = 0;
        Integer saveCurr = null;
        JavaAST javaAST;
        ParserIterator iterator = new ParserIterator(this);

        private ParserHelper() {
            tokens = lexer.getTokens()
                    .stream()
                    .filter(token -> token.getType() != TokenType.BlockComment && token.getType() != TokenType.Comment)
                    .collect(Collectors.toList());
        }

        /**
         * 存档指针，仅保存一次的
         * */
        public void saveIdx() {
            saveCurr = curr;
        }

        /**
         * 读取指针，读取前需要保存，仅支持读取一次
         * */
        public void readIdx() {
            if (saveCurr == null) {
                throw new SystemException("parser idx did not save");
            }
            curr = saveCurr;
            saveCurr = null;
        }

        public JavaAST parse(ParserIterator iterator) {
            javaAST = new JavaAST();
            curr = 0;
            while (curr < tokens.size()) {
                parseProgram();
            }
            return javaAST;
        }

        private Expression getExpression() {
            curr = 0;
            return parseExpression();
        }

        private void parseProgram() {
            while (curr < tokens.size()) {
                Token dealToken = tokens.get(curr);
                if (dealToken.getType() == TokenType.Words) {
                    String value = dealToken.getValue();
                    if ("package".equals(dealToken.getValue())) {
                        Package node = parsePackage();
                        javaAST.setPackageNode(node);
                        curr++;
                        continue;
                    }
                    if ("import".equals(value)) {
                        Import node = parseImport();
                        javaAST.getImports().add(node);
                        curr++;
                        continue;
                    }
                    List<Annotation> annotations = getAnnotations();
                    // TODO final,abstract 关键字待支持
                    Modifier modifier = parseModifierAndNext();
                    dealToken = tokens.get(curr);
                    value = dealToken.getValue();
                    if ("class".equals(value)) {
                        Class clazz = parseClass();
                        clazz.setModifier(modifier);
                        clazz.setAnnotations(annotations);
                        clazz.setClassFullName(javaAST.getPackageNode().getValue()
                                + "." + clazz.getClassName());
                        javaAST.getClassList().add(clazz);
                        curr++;
                        continue;
                    }
                }
                throwIllegalToken(dealToken.getValue());
            }
        }

        /**
         * 解析注解，并指向下一位
         * */
        private List<Annotation> getAnnotations() {
            List<Annotation> annotations = new ArrayList<>();
            while (curr < tokens.size()) {
                Token token = tokens.get(curr);
                if (token.getValue().startsWith("@")) {
                    String annotationName = token.getValue();
                    List<Expression> expressions = Collections.emptyList();
                    if (isNext(TokenType.LParen)) {
                        /*
                          移动至param
                          @annotation ( param )  ->  @annotation ( param )
                          ^                                      ^
                         * */
                        curr++;
                        expressions = parameterPassing();
                    }
                    Annotation annotation = new Annotation(annotationName, expressions);
                    annotations.add(annotation);
                    curr++;
                } else {
                    break;
                }
            }
            return annotations;
        }

        /**
         * 解析方法传参 (e1,e2)
         * */
        private List<Expression> parameterPassing() {

            List<Expression> expressions = new ArrayList<>();
            if (isNext(TokenType.RParen)) {
                curr++;
                return expressions;
            }
            while (true) {
                needNext();
                Expression expression = parseExpression();
                expressions.add(expression);
                if (!isNext(TokenType.Symbol, COMMA)) {
                    break;
                }
                needNext();
            }
            needNext(TokenType.RParen);
            return expressions;
        }

        /**
         * 解析方法参数定义
         * */
        private List<VarDefineStatement> parameterDefine() {
            List<VarDefineStatement> paramDefines = new ArrayList<>();
            if (isNext(TokenType.RParen)) {
                curr++;
                return paramDefines;
            }
            while (true) {
                needNext();
                VarDefineStatement varDefine = parseVarDefine();
                paramDefines.add(varDefine);
                if (!isNext(TokenType.Symbol, COMMA)) {
                    break;
                }
                needNext();
            }
            needNext(TokenType.RParen);
            return paramDefines;
        }

        private Package parsePackage() {
            Token token = needNext(TokenType.Words);
            Package aPackage = new Package();
            String value = parseWordsPoint();
            needNext(TokenType.Symbol, SEMICOLONS);
            aPackage.setValue(value);
            return aPackage;
        }

        /**
         * 获取如 xx.xx.xx 的字符
         * */
        private String parseWordsPoint() {
            Token token = tokens.get(curr);
            if (token.getType() != TokenType.Words) {
                throwParserErr(TokenType.Words, token.getType());
            }
            StringBuilder value = new StringBuilder(token.getValue());
            while (isNext(TokenType.Symbol, POINT)) {
                needNext(TokenType.Symbol, POINT);
                Token next = needNext(TokenType.Words);
                value.append(POINT).append(next.getValue());
            }
            return value.toString();
        }

        private Import parseImport() {
            Import anImport = new Import();
            if (isNext(TokenType.Words, "static")) {
                // 处理 import static xxx
                anImport.setStaticImp(true);
                needNext();
            }
            Token token = needNext(TokenType.Words);
            StringBuilder value = new StringBuilder(token.getValue());
            while (!isNext(TokenType.Symbol, SEMICOLONS)) {
                needNext(TokenType.Symbol, POINT);
                Token next = needNext();
                value.append(POINT).append(next.getValue());
                if (next.getType() != TokenType.Words) {
                    if ("*".equals(next.getValue())) {
                        // import xx.xx.* 最后一个为*
                        break;
                    } else {
                        throwIllegalToken(next.getValue());
                    }
                }
            }
            needNext(TokenType.Symbol, SEMICOLONS);
            anImport.setValue(value.toString());
            return anImport;
        }

        private Class parseClass() {

            Token className = needNext(TokenType.Words);
            Class clazz = new Class(className.getValue());

            if (isNext(TokenType.Words)) {
                Token next = needNext(TokenType.Words);
                if ("extends".equals(next.getValue())) {
                    needNext(TokenType.Words);
                    clazz.setSuperClass(parseWordsPoint());

                    if (isNext(TokenType.Words, "implements")) {
                        needNext(TokenType.Words, "implements");
                        parseImplements(clazz);
                    }
                } else if ("implements".equals(next.getValue())) {
                    parseImplements(clazz);
                }
            }

            needNext(TokenType.LBrace);
            // 成员变量
            List<Member> members = new ArrayList<>();
            clazz.setMembers(members);
            // 构造器
            List<Method> constructors = new ArrayList<>();
            clazz.setConstructors(constructors);
            // 方法
            List<Method> methods = new ArrayList<>();
            clazz.setMethods(methods);

            Token token;
            while ((token = readNext()) != null && token.getType() != TokenType.RBrace) {
                // 解析注解
                List<Annotation> annotations = getAnnotations();
                // 解析修饰符 如：public static synchronized
                Modifier modifier = parseModifierAndNext();
                saveIdx();
                // 判断是成员变量还是方法或构造器
                // 1.解析 parseWordsPoint 前存档，因为解析构造器和方法时会再次执行parseWordsPoint
                // 2.调用 parseWordsPoint 后再判断是因为 如 com.xx.xxx 会影响判断
                parseWordsPoint();
                if (isNext(TokenType.LParen) ||
                        isNext(2, TokenType.LParen, TokenType.LParen.getFixValue())) {
                    readIdx();
                    // 后一位或后两位是括号，则为方法定义
                    // TODO 抽象方法
                    Method method;
                    if (isCurr(TokenType.Words, className.getValue())) {
                        // 解析构造器
                        method = parseConstructor();
                        constructors.add(method);
                    } else {
                        // 解析方法
                        method = parseMethod();
                        methods.add(method);
                    }

                    method.setModifier(modifier);
                    method.setAnnotations(annotations);
                } else {
                    readIdx();
                    // 解析成员变量
                    // TODO 成员注解
                    Member member = parseMember(modifier);
                    members.add(member);
                }

                // TODO 解析静态代码块
            }
            if (token == null) {
                throw new RuntimeException("缺少" + TokenType.RBrace + "值 " + TokenType.RBrace.getFixValue());
            }
            return clazz;
        }

        private Member parseMember(Modifier modifier) {
            VarDefineStatement varDefine = parseVarDefineAndAssign();
            needNext(TokenType.Symbol, SEMICOLONS);
            return new Member(modifier, varDefine);
        }

        private Modifier parseModifierAndNext() {
            Modifier modifier = new Modifier();
            AccessPrivilege accessPrivilege = getAccessPrivilegeAndNext();
            modifier.setAccessPrivilege(accessPrivilege);

            for (String value = tokens.get(curr).getValue();modifier.accept(value);) {
                // 匹配到修饰符，指针加一
                curr++;
                value = tokens.get(curr).getValue();
            }

            return modifier;
        }

        private AccessPrivilege getAccessPrivilegeAndNext() {
            AccessPrivilege accessPrivilege = AccessPrivilege.of(tokens.get(curr));
            if (accessPrivilege == null) {
                accessPrivilege = AccessPrivilege.DEFAULT;
            } else {
                curr++;
            }
            return accessPrivilege;
        }

        private void parseImplements(Class clazz) {
            List<String> implementsList = new ArrayList<>();
            needNext(TokenType.Words);
            implementsList.add(parseWordsPoint());
            while (isNext(TokenType.Symbol, COMMA)) {
                needNext(TokenType.Symbol, COMMA);
                needNext(TokenType.Words);
                implementsList.add(parseWordsPoint());
            }
            clazz.setImplementClasses(implementsList);
        }

        /**
         * returnType methodName(...) {...}
         * */
        private Method parseMethod() {
            Method method = new Method();

            // 目前解析 public void getValue(...) {...}
            String returnType = parseWordsPoint();
            Token methodName = needNext(TokenType.Words);
            needNext(TokenType.LParen);

            List<VarDefineStatement> varDefineStatements = parameterDefine();
            if (isNext(TokenType.Words, "throws")) {
                // 处理方法异常抛出
                method.setThrowList(parseThrowList());
            }
            // TODO 抽象方法无代码体
            needNext(TokenType.LBrace);
            CodeBlock codeBlock = parseCodeBlock();

            method.setMethodName(methodName.getValue());
            method.setReturnType(returnType);
            method.setMethodParams(varDefineStatements);
            method.setCodeBlock(codeBlock);
            return method;
        }

        private Constructor parseConstructor() {
            String returnType = tokens.get(curr).getValue();
            Constructor constructor = new Constructor(returnType);
            needNext(TokenType.LParen);

            List<VarDefineStatement> varDefineStatements = parameterDefine();
            if (isNext(TokenType.Words, "throws")) {
                // 处理方法异常抛出
                constructor.setThrowList(parseThrowList());
            }

            needNext(TokenType.LBrace);
            CodeBlock codeBlock = parseCodeBlock();

            constructor.setReturnType(returnType);
            constructor.setMethodParams(varDefineStatements);
            constructor.setCodeBlock(codeBlock);
            return constructor;
        }

        private List<String> parseThrowList() {
            Token token = tokens.get(curr);
            if ("throws".equals(token.getValue())) {
                // 处理方法异常抛出
                throwIllegalToken(token.getValue());
            }
            List<String> throwList = new ArrayList<>();
            do {
                needNext();
                Token throwE = needNext(TokenType.Words);
                throwList.add(throwE.getValue());
            } while (isNext(TokenType.Symbol, COMMA));
            return throwList;
        }

        private CodeBlock parseCodeBlock() {
            CodeBlock codeBlock = new CodeBlock();
            Token token = tokens.get(curr);
            if (token.getType() != TokenType.LBrace) {
                Statement statement = parseStatement();
                codeBlock.getStatements().add(statement);
                return codeBlock;
            }
            // 跳过 {
            while (needNext().getType() != TokenType.RBrace) {
                Statement statement = parseStatement();
                codeBlock.getStatements().add(statement);
            }
            return codeBlock;
        }

        private Statement parseStatement() {
            Token token = tokens.get(curr);
            if (SEMICOLONS.equals(token.getValue())) {
                // ; skip
                // 空语句
                return EMPTY;
            }
            if (isKeywords(token)) {
                // 关键字语句
                return parseKeywordStatement();
            }
            if (token.getType() == TokenType.LBrace) {
                // 代码块
                return parseCodeBlock();
            }
            Statement statement = parseStatementLine();
            needNext(TokenType.Symbol, SEMICOLONS);
            return statement;
        }

        /**
         * 解析一般单行语句，不包含结束符 ;
         * */
        private Statement parseStatementLine() {
            Token token = tokens.get(curr);
            if (token.getType() == TokenType.Words) {
                saveIdx();
                String wordsPoint = parseWordsPoint();
                if (isNext(TokenType.Words)) {
                    readIdx();
                    // 变量定义
                    return parseVarDefineAndAssign();
                }
                if (isNext(TokenType.Symbol)) {
                    String varName = wordsPoint;
                    if (isNext(TokenType.Symbol, EQUAL)) {
                        // 变量赋值
                        // 指向等于号后面的字符
                        needNext();
                        needNext();
                        Expression expression = parseExpression();
                        return new VarAssignStatement(varName, expression);
                    } else if (isNext(TokenType.LParen)) {
                        // 下标回溯
                        readIdx();
                        // 链式调用表达式语句，方法调用
                        Expression expression = parseExpression();
                        return new ExpStatement(expression);
                    } else {
                        Token next = needNext();
                        if (isOperator(next)) {
                            String operator = next.getValue();
                            if (isNext(TokenType.Symbol, EQUAL)) {
                                // 变量赋值 a += exp
                                needNext();
                                needNext();
                                Expression expression = parseExpression();
                                BinaryOperate binaryOperate = new BinaryOperate(new Identifier(varName), operator, expression);
                                return new VarAssignStatement(varName, binaryOperate);
                            }
                            // a++,a--
                            if (!isPlusMinus(operator)) {
                                throwIllegalToken(operator);
                            }
                            needNext(TokenType.Symbol, operator);
                            UnaryOperate unaryOperate = new UnaryOperate(
                                    operator + operator,
                                    new Identifier(varName),
                                    false
                            );
                            return new ExpStatement(unaryOperate);
                        } else {
                            // TODO 其他符号
                            throwIllegalToken(token.getValue());
                        }

                    }
                }
                if (isNext(TokenType.LParen)) {
                    // 表达式，方法调用开头
                    readIdx();
                    Expression expression = parseExpression();
                    return new ExpStatement(expression);
                }
                // TODO 数组、泛形解析
                throwIllegalToken(token.getValue());
            } else if (token.getType() == TokenType.Symbol
                    || token.getType() == TokenType.Number
                    || token.getType() == TokenType.String
                    || token.getType() == TokenType.Character) {
                // ++,-- operator
                // 字面量
                // others throw
                Expression expression = parseExpression();
                return new ExpStatement(expression);
            } else {
                // TODO lambda表达式
                throwIllegalToken(token.getValue());
            }
            return EMPTY;
        }

        private VarDefineStatement parseVarDefineAndAssign() {
            return new VarDefineAssignParser().parse(iterator);
        }

        private boolean isOperator(Token token) {
            String value = token.getValue();
            return GrammarUtil.isOperator(value);
        }

        private VarDefineStatement parseVarDefine() {
            return new VarDefineParser().parse(iterator);
        }

        private Statement parseKeywordStatement() {
            Token token = tokens.get(curr);
            String value = token.getValue();
            if ("if".equals(value)) {
                return parseIf();
            }
            if ("for".equals(value)) {
                return parseFor();
            }
            if ("while".equals(value)) {
                return parseWhile();
            }
            if ("do".equals(value)) {
                return parseDoWhile();
            }
            if ("new".equals(value)) {
                Expression expression = parseExpression();
                needNext(TokenType.Symbol, SEMICOLONS);
                return new ExpStatement(expression);
            }
            if ("return".equals(value)) {
                if (isNext(TokenType.Symbol, SEMICOLONS)) {
                    // 直接return的情况
                    needNext(TokenType.Symbol, SEMICOLONS);
                    return new ReturnStatement();
                }
                // return具体的值
                needNext();
                Expression expression = parseExpression();
                needNext(TokenType.Symbol, SEMICOLONS);
                return new ReturnStatement(expression);
            }
            if ("try".equals(value)) {
                TryParser tryParser = new TryParser();
                return tryParser.parse(iterator);
            }
            throwIllegalToken(value);
            return null;
        }

        private boolean isKeywords(Token token) {
            return TokenType.Words == token.getType()
                    && GrammarUtil.isCodeKeywords(token.getValue());
        }

        private Statement parseIf() {
            needNext(TokenType.LParen);
            needNext();
            Expression condition = parseExpression();
            needNext(TokenType.RParen);
            needNext();
            Statement trueStatement = parseCodeBlock();
            Statement falseStatement = null;
            while (isNext(TokenType.Words, "else")) {
                readNext();
                if (isNext(TokenType.Words, "if")) {
                    readNext();
                    falseStatement = parseIf();
                } else {
                    needNext();
                    falseStatement = parseCodeBlock();
                    break;
                }
            }
            IfStatement ifStatement = new IfStatement(condition, trueStatement);
            ifStatement.setFalseStatement(falseStatement);
            return ifStatement;
        }

        private Statement parseWhile() {
            needNext(TokenType.LParen);
            needNext();
            Expression condition = parseExpression();
            needNext(TokenType.RParen);
            needNext(TokenType.LBrace);
            CodeBlock loopBody = parseCodeBlock();
            return new WhileStatement(condition, loopBody);
        }

        private Statement parseDoWhile() {
            needNext(TokenType.LBrace);
            CodeBlock loopBody = parseCodeBlock();
            needNext(TokenType.Words, "while");
            needNext(TokenType.LParen);
            needNext();
            Expression condition = parseExpression();
            needNext(TokenType.RParen);
            needNext(TokenType.Symbol, SEMICOLONS);
            return new WhileStatement(condition, loopBody);
        }

        private Statement parseFor() {
            needNext(TokenType.LParen);
            needNext();
            // parseStatement已经包含读取分号
            Statement beforeStatement = parseStatementLine();
            LoopStatement loopStatement;
            if (isNext(TokenType.Symbol, SEMICOLONS)) {
                // 普通for循环
                needNext();
                needNext();
                Expression loopCondition = parseExpression();
                needNext(TokenType.Symbol, SEMICOLONS);
                Statement afterStatement;
                if (isNext(TokenType.RParen)) {
                    afterStatement = EMPTY;
                } else {
                    needNext();
                    afterStatement = parseStatementLine();
                }
                loopStatement = new ForStatement(beforeStatement, afterStatement, loopCondition);
            } else {
                needNext(TokenType.Symbol, COLON);
                Token token = needNext(TokenType.Words);
                AssertUtil.isTrue(beforeStatement instanceof VarDefineStatement,
                        "is not var define:" + beforeStatement.getStatementSource());
                loopStatement = new EnhanceForStatement((VarDefineStatement) beforeStatement,
                        new Identifier(token.getValue()));
            }
            needNext(TokenType.RParen);
            needNext(TokenType.LBrace);
            CodeBlock loopBody = parseCodeBlock();
            loopStatement.setLoopBody(loopBody);
            return loopStatement;
        }

        private Expression parseObjectNew() {
            Token className = needNext(TokenType.Words);
            ConstructorCall constructorCall = new ConstructorCall(className.getValue());
            needNext(TokenType.LParen);
            List<Expression> expressions = parameterPassing();
            constructorCall.setPassedParams(expressions);
            return constructorCall;
        }

        /**
         * 解析表达式
         * */
        private Expression parseExpression() {
            Token token = tokens.get(curr);
            if (token.getType() == TokenType.LParen) {
                // 左括号
                if (isNext(2, TokenType.RParen, TokenType.RParen.getFixValue())) {
                    // 1.括号直接仅包裹一个单词，类型强转
                    // TODO 泛形处理
                    String typeName = needNext(TokenType.Words).getValue();
                    needNext(TokenType.RParen);
                    needNext();
                    return new TypeCast(typeName, parseExpression());
                }
                // 2.表达式括号包裹
                needNext();
                Expression expression = parseExpression();
                needNext(TokenType.RParen);
                return parseToEnd(new PriorityExpression(expression));
            }
            if (token.getType() == TokenType.Words) {
                if ("new".equals(token.getValue())) {
                    // 创建对象
                    Expression expression = parseObjectNew();
                    return parseToEnd(expression);
                }
                if (isNext(TokenType.LParen)) {
                    // 方法调用
                    Expression expression = parseMethodCall();
                    return parseToEnd(expression);
                }
                Identifier identifier = new Identifier(token.getValue());
                if (isExpressionEnd()) {
                    // 标识符
                    return identifier;
                }
                Token next = needNext();
                String value = next.getValue();
                if (isOperator(next)) {
                    if (isNext(TokenType.Symbol, value)) {
                        if (isPlusMinus(value)) {
                            // TODO support like: i ++ + ++ j
                            // 单目 i++
                            curr++;
                            Expression expression = new UnaryOperate(value + value, identifier, false);
                            return parseToEnd(expression);
                        }
                    }
                    // 双目
                    curr--;
                    return parseBinary(identifier);
                }
                if (isCompare(value) || EQUAL.equals(value)) {
                    String compare = getCompare();
                    needNext();
                    Expression expression = parseExpression();
                    return new BinaryOperate(identifier, compare, expression);
                }
                // todo 数组、泛形 待支持
                curr--;
                return parseToEnd(identifier);
            }
            if (token.getType() == TokenType.Symbol) {
                Expression expression = parseUnaryOperate();
                return parseToEnd(expression);
            }
            if (token.getType() == TokenType.String) {
                Expression expression = new StringLiteral(token.getValue());
                return parseToEnd(expression);
            }
            if (token.getType() == TokenType.Number) {
                Expression expression = new NumberLiteral(token.getValue());
                return parseToEnd(expression);
            }
            throwIllegalToken(token.getValue());
            return null;
        }

        private Expression parseToEnd(Expression expression) {
            if (isExpressionEnd()) {
                return expression;
            }
            if (isNext(TokenType.Symbol, POINT)) {
                Expression callChain = parseCallChain(expression);
                return parseToEnd(callChain);
            }
            // 双目
            return parseBinary(expression);
        }

        private Expression parseCallChain(Expression first) {
            if (!isNext(TokenType.Symbol, POINT)) {
                return first;
            }
            needNext(TokenType.Symbol, POINT);
            Token token = needNext(TokenType.Words);
            if (isNext(TokenType.LParen)) {
                Expression next = parseMethodCall();
                return new CallChain(first, next);
            }
            Identifier next = new Identifier(token.getValue());
            Expression nextChain = parseCallChain(next);
            return new CallChain(first, nextChain);
        }

        private boolean isCompare(String value) {
            return ">".equals(value)
                    || "<".equals(value)
                    || EQUAL.equals(value)
                    || ("!".equals(value) && isNext(TokenType.Symbol, EQUAL))
                    ;
        }

        private String getCompare() {
            Token token = tokens.get(curr);
            if (token == null) {
                throwIllegalToken("");
            }
            String value = token.getValue();
            if (!isCompare(value)) {
                throwIllegalToken(value);
            }
            if (EQUAL.equals(value)) {
                needNext(TokenType.Symbol, EQUAL);
                // ==
                return EQUAL + EQUAL;
            }
            if (isNext(TokenType.Symbol, EQUAL)) {
                // >=,<=,!=
                needNext();
                return value + EQUAL;
            }
            // >,<
            return value;
        }

        private boolean isPlusMinus(String value) {
            return "+".equals(value) || "-".equals(value);
        }

        /**
         * 解析表达式中的操作符
         * +,-,*,/,&,|,^
         * &&,||
         * >=, <=, ==, !=
         * */
        private String parseOperator() {
            Token token = tokens.get(curr);
            String operator = token.getValue();
            if (!isOperator(token)) {
                if (isCompare(operator)) {
                    return getCompare();
                }
                throwIllegalToken(token.getValue());
            }
            if ("&".equals(operator) || "|".equals(operator)) {
                if (isNext(TokenType.Symbol, operator)) {
                    curr++;
                    operator += operator;
                }
            }
            return operator;
        }

        private Expression parseMethodCall() {
            Token token = tokens.get(curr);
            String calledMethod = token.getValue();
            needNext(TokenType.LParen);
            List<Expression> expressions = parameterPassing();
            MethodCall methodCall = new MethodCall(calledMethod);
            methodCall.setPassedParams(expressions);
            return methodCall;
        }

        /**
         * 单目运算符 --,++,!
         * */
        private Expression parseUnaryOperate() {
            Token token = tokens.get(curr);
            String value = token.getValue();
            if ("!".equals(value)) {
                needNext();
                Expression expression = parseExpression();
                return new NotOperate(expression);
            }
            if ("-".equals(value) || "+".equals(value)) {
                needNext(TokenType.Symbol, value);
                Token next = needNext(TokenType.Words);
                return new UnaryOperate(value + value,
                        new Identifier(next.getValue()));
            }
            throwIllegalToken(value);
            return null;
        }

        /**
         * 双目运算符
         * */
        private Expression parseBinary(Expression expression) {
            needNext();
            String operator = parseOperator();
            curr++;
            Expression right = parseExpression();
            return new BinaryOperate(expression, operator, right);
        }

        private boolean isExpressionEnd() {
            return isNext(TokenType.RParen)
                    || isNext(TokenType.RBrace)
                    || isNext(TokenType.Symbol, SEMICOLONS)
                    || isNext(TokenType.Symbol, COMMA)
                    || curr == tokens.size() - 1
                    ;
        }

        /**
         * 判断下一个token，不移动指针
         */
        private boolean isCurr(TokenType type, String value) {
            Token currToken = tokens.get(this.curr);
            return currToken != null && currToken.getType() == type && value.equals(currToken.getValue());
        }

        /**
         * 判断下一个token，不移动指针
         */
        private boolean isNext(TokenType type) {
            Token next = peekNext();
            return next != null && next.getType() == type;
        }

        /**
         * 判断下一个token，不移动指针
         */
        private boolean isNext(TokenType type, String value) {
            return isNext(1, type, value);
        }

        /**
         * 判断下n个token，不移动指针
         */
        private boolean isNext(int n, TokenType type, String value) {
            Token next = peekNext(n);
            return next != null && next.getType() == type && value.equals(next.getValue());
        }

        /**
         * 窥探下一个token，不移动指针
         */
        private Token peekNext() {
            return peekNext(1);
        }

        /**
         * 窥探下一个token，不移动指针
         */
        private Token peekNext(int n) {
            int peekPoint = curr + n;
            if (peekPoint < tokens.size()) {
                return tokens.get(peekPoint);
            }
            return null;
        }

        /**
         * 读取至某个类型前，移动指针
         */
        private List<Token> readBefore(TokenType type) {
            List<Token> beforeTokens = new ArrayList<>();
            while (true) {
                beforeTokens.add(this.tokens.get(curr));
                if (curr == tokens.size() - 1 || isNext(type)) {
                    break;
                }
                curr++;
            }
            return beforeTokens;
        }

        /**
         * 读取至某个类型前，移动指针
         */
        private void readBefore(TokenType type, Consumer<Token> consumer) {
            while (true) {
                consumer.accept(tokens.get(curr));
                if (curr == tokens.size() - 1 || isNext(type)) {
                    break;
                }
                curr++;
            }
        }

        /**
         * 读取下一个token，移动指针
         */
        private Token readNext() {
            curr++;
            if (curr < tokens.size()) {
                return tokens.get(curr);
            }
            return null;
        }

        /**
         * 需要的下一个token，移动指针
         */
        private Token needNext() {
            Token token = readNext();
            if (token == null) {
                printParsed();
                throw new RuntimeException("字符不完整");
            }
            return token;
        }

        /**
         * 需要的下一个token，移动指针，判断类型
         */
        private Token needNext(TokenType type) {
            Token token = readNext();
            if (token == null) {
                printParsed();
                throw new RuntimeException("缺少" + type + "值 " + type.getFixValue());
            }
            if (token.getType() != type) {
                printParsed();
                throw new RuntimeException("缺少" + type + "值 " + type.getFixValue() + " 输入" + token.getType() + "值 " + token.getValue());
            }
            return token;
        }

        /**
         * 需要的下一个token，移动指针，判断类型
         */
        private Token needNext(TokenType type, String value) {
            Token token = readNext();
            if (token == null) {
                throwParserErr(type, value);
            }
            if (token.getType() != type) {
                throwParserErr(type, token.getType(), value, token.getValue());
            }
            if (!token.getValue().equals(value)) {
                throwParserErr(type, token.getType(), value, token.getValue());
            }
            return token;
        }

        private void throwIllegalToken(String value) {
            printParsed();
            throw new RuntimeException("非法字符 " + value);
        }

        private void throwParserErr(TokenType needType, String needValue) {
            printParsed();
            throw new RuntimeException("缺少" + needType + "值 " + needValue);
        }

        private void throwParserErr(TokenType needType, TokenType inputType) {
            throwParserErr(needType, inputType, needType.getFixValue(), inputType.getFixValue());
        }

        private void throwParserErr(TokenType needType, TokenType inputType,
                                    String needValue) {
            throwParserErr(needType, inputType, needValue, inputType.getFixValue());
        }

        private void throwParserErr(TokenType needType, TokenType inputType,
                                    String needValue, String inputValue) {
            printParsed();
            throw new RuntimeException("缺少" + needType + "值 " + needValue
                    + " 输入" + inputType + "值 " + inputValue);
        }

        private void printParsed() {
            StringBuilder parsed = new StringBuilder();
            for (int i = 0; i < curr && i < tokens.size(); i++) {
                parsed.append(tokens.get(i).showCode() + "\n");
            }
            System.out.println("当前已解析语法");
            System.out.println(parsed.toString());
        }

    }

    public class ParserIterator {
        ParserHelper helper;

        public ParserIterator(ParserHelper helper) {
            this.helper = helper;
        }

        public Expression parseExpression() {
            return helper.parseExpression();
        }

        public CodeBlock parseBlock() {
            return helper.parseCodeBlock();
        }

        public Statement parseStatement() {
            return helper.parseStatement();
        }

        /**
         * 获取如 xx.xx.xx 的字符
         * */
        public String parseWordsPoint() {
            return helper.parseWordsPoint();
        }

        public Token getCurr() {
            return helper.tokens.get(helper.curr);
        }

        public boolean isCurr(TokenType type, String value) {
            return helper.isCurr(type, value);
        }

        /**
         * 判断下一个token，不移动指针
         */
        public boolean isNext(TokenType type) {
            return helper.isNext(type);
        }

        /**
         * 判断下一个token，不移动指针
         */
        public boolean isNext(TokenType type, String value) {
            return helper.isNext(1, type, value);
        }

        /**
         * 判断下n个token，不移动指针
         */
        public boolean isNext(int n, TokenType type, String value) {
            return helper.isNext(n, type, value);
        }

        /**
         * 窥探下一个token，不移动指针
         */
        public Token peekNext() {
            return helper.peekNext(1);
        }

        /**
         * 窥探下一个token，不移动指针
         */
        public Token peekNext(int n) {
            return helper.peekNext(n);
        }

        /**
         * 读取下一个token，移动指针
         */
        public Token readNext() {
            return helper.readNext();
        }

        /**
         * 需要的下一个token，移动指针
         */
        public Token needNext() {
            return helper.needNext();
        }

        /**
         * 需要的下一个token，移动指针，判断类型
         */
        public Token needNext(TokenType type) {
            return helper.needNext(type);
        }

        /**
         * 需要的下一个token，移动指针，判断类型
         */
        public Token needNext(TokenType type, String value) {
            return helper.needNext(type, value);
        }
    }
}
