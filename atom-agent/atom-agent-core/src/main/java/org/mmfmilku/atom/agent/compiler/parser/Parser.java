package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Package;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.*;

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
public class Parser {

    private static final String SEMICOLONS = ";";

    private static final String COMMA = ",";

    private static final String EQUAL = "=";

    private static Statement EMPTY = new CodeBlock();

    private Lexer lexer;

    private JavaFile javaFile;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Node execute() {
        ParserHandle handle = new ParserHandle();
        handle.execute();
        return javaFile;
    }

    private class ParserHandle {
        List<Token> tokens;
        int curr = 0;
        int peekPoint = 0;
        Token dealToken;

        private ParserHandle() {
            tokens = lexer.getTokens()
                    .stream()
                    .filter(token -> token.getType() != TokenType.BlockComment && token.getType() != TokenType.Comment)
                    .collect(Collectors.toList());
            javaFile = new JavaFile();
        }

        private void execute() {
            while (curr < tokens.size()) {
                dealToken = tokens.get(curr);
                parseProgram();
            }
        }

        private void parseProgram() {
            while (curr < tokens.size()) {
                dealToken = tokens.get(curr);
                if (dealToken.getType() == TokenType.Words) {
                    String value = dealToken.getValue();
                    if ("package".equals(dealToken.getValue())) {
                        Package node = parsePackage();
                        javaFile.setPackageNode(node);
                        curr++;
                        continue;
                    }
                    if ("import".equals(value)) {
                        Import node = parseImport();
                        javaFile.getImports().add(node);
                        curr++;
                        continue;
                    }
                    List<Annotation> annotations = getAnnotations();
                    // TODO final,abstract 关键字待支持
                    dealToken = tokens.get(curr);
                    value = dealToken.getValue();
                    Modifier modifier = Modifier.DEFAULT;
                    if (isModifier(dealToken)) {
                        modifier = Modifier.of(dealToken.getValue());
                        Token next = needNext(TokenType.Words, "class");
                        value = next.getValue();
                    }
                    if ("class".equals(value)) {
                        Class clazz = parseClass();
                        clazz.setModifier(modifier);
                        clazz.setAnnotations(annotations);
                        javaFile.getClassList().add(clazz);
                        curr++;
                        continue;
                    }
                }
                throwIllegalToken(dealToken.getValue());
            }
        }

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

        private Modifier getModifierAndNext() {
            Modifier modifier = Modifier.of(tokens.get(curr));
            if (modifier == null) {
                modifier = Modifier.DEFAULT;
            } else {
                curr++;
            }
            return modifier;
        }

        private boolean isModifier(Token dealToken) {
            Modifier modifier = Modifier.of(dealToken.getValue());
            return modifier != null;
        }

        private Package parsePackage() {
            Token token = needNext(TokenType.Words);
            Package aPackage = new Package();
            aPackage.setValue(token.getValue());
            needNext(TokenType.Symbol, SEMICOLONS);
            return aPackage;
        }

        private Import parseImport() {
            Import anImport = new Import();
            if (isNext(TokenType.Words, "static")) {
                // 处理 import static xxx
                anImport.setStaticImp(true);
                needNext();
            }
            Token token = needNext(TokenType.Words);
            Token symbol = needNext(TokenType.Symbol);
            if ("*".equals(symbol.getValue())) {
                // import xx.xx.*;
                anImport.setValue(token.getValue() + symbol.getValue());
                needNext(TokenType.Symbol, SEMICOLONS);
            } else {
                anImport.setValue(token.getValue());
            }
            return anImport;
        }

        private Class parseClass() {
            Class clazz = new Class();

            Token className = needNext(TokenType.Words);
            clazz.setClassName(className.getValue());

            needNext(TokenType.LBrace);

            List<Method> methods = new ArrayList<>();
            clazz.setMethods(methods);
            Token token;
            while ((token = readNext()) != null && token.getType() != TokenType.RBrace) {
                List<Annotation> annotations = getAnnotations();
                // 解析方法
                Method method = parseMethod();
                method.setAnnotations(annotations);
                methods.add(method);
                // TODO 解析成员变量
                // TODO 解析成构造器
                // TODO 解析静态代码块
            }
            if (token == null) {
                throw new RuntimeException("缺少" + TokenType.RBrace + "值 " + TokenType.RBrace.getFixValue());
            }
            curr++;
            return clazz;
        }

        private Method parseMethod() {
            Method method = new Method();

            // 目前解析 public void getValue(...) {...}
            Modifier modifier = getModifierAndNext();
            // todo 添加 static,final
            Token returnType = tokens.get(curr);
            Token methodName = needNext(TokenType.Words);
            needNext(TokenType.LParen);
            // TODO 方法参数
            List<VarDefineStatement> varDefineStatements = parameterDefine();
            if (isNext(TokenType.Words, "throws")) {
                // 处理方法异常抛出
                method.setThrowList(new ArrayList<>());
                do {
                    needNext();
                    Token throwE = needNext(TokenType.Words);
                    method.getThrowList().add(throwE.getValue());
                } while (isNext(TokenType.Symbol, COMMA));
            }
            // TODO 抽象方法无代码体
            needNext(TokenType.LBrace);
            // TODO 代码体
            CodeBlock codeBlock = parseCodeBlock();

            method.setMethodName(methodName.getValue());
            method.setModifier(modifier);
            method.setReturnType(returnType.getValue());
            method.setMethodParams(varDefineStatements);
            method.setCodeBlock(codeBlock);
            return method;
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
            if (token.getType() == TokenType.Words) {
                if (isKeywords(token)) {
                    return parseKeyword();
                }
                if (isNext(TokenType.Words)) {
                    // 变量定义
                    VarDefineStatement varDefine = parseVarDefine();
                    token = needNext(TokenType.Symbol);
                    if (SEMICOLONS.equals(token.getValue())) {
                        // 仅定义变量
                        // nop
                    } else if (EQUAL.equals(token.getValue())) {
                        // 变量定义并且赋值 =
                        needNext();
                        Expression expression = parseExpression();
                        varDefine.setAssignExpression(expression);
                        needNext(TokenType.Symbol, SEMICOLONS);
                    } else {
                        // todo 多变量定义 int a,b;
                        throwIllegalToken(token.getValue());
                    }
                    // 变量定义
                    return varDefine;
                }
                if (isNext(TokenType.Symbol)) {
                    String varName = token.getValue();
                    if (isNext(TokenType.Symbol, EQUAL)) {
                        // 变量赋值
                        // 指向等于号后面的字符
                        needNext();
                        needNext();
                        Expression expression = parseExpression();
                        needNext(TokenType.Symbol, SEMICOLONS);
                        return new VarAssignStatement(varName, expression);
                    } else {
                        Token next = needNext();
                        if (isOperator(next)) {
                            String operator = next.getValue();
                            // 变量赋值 a += exp
                            if (isNext(TokenType.Symbol, EQUAL)) {
                                needNext();
                                needNext();
                                Expression expression = parseExpression();
                                needNext(TokenType.Symbol, SEMICOLONS);
                                BinaryOperate binaryOperate = new BinaryOperate(new Identifier(varName), operator, expression);
                                return new VarAssignStatement(varName, binaryOperate);
                            }
                            // a++,a--
                            if (!isPlusMinus(operator)) {
                                throwIllegalToken(operator);
                            }
                            needNext(TokenType.Symbol, operator);
                            needNext(TokenType.Symbol, SEMICOLONS);
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
                    Expression expression = parseExpression();
                    needNext(TokenType.Symbol, SEMICOLONS);
                    return new ExpStatement(expression);
                }
                // TODO 数组、泛形解析
                throwIllegalToken(token.getValue());
            } else if (token.getType() == TokenType.Symbol) {
                if (SEMICOLONS.equals(token.getValue())) {
                    // ; skip
                    return EMPTY;
                }
                // ++,-- operator
                // others throw
                Expression expression = parseExpression();
                needNext(TokenType.Symbol, SEMICOLONS);
                return new ExpStatement(expression);
            } else if (token.getType() == TokenType.LBrace) {
                return parseCodeBlock();
            } else if (token.getType() == TokenType.Number
                    || token.getType() == TokenType.String
                    || token.getType() == TokenType.Character) {
                Expression expression = parseExpression();
                needNext(TokenType.Symbol, SEMICOLONS);
                return new ExpStatement(expression);
            } else {
                // TODO lambda表达式
                throwIllegalToken(token.getValue());
            }
            return EMPTY;
        }

        private boolean isOperator(Token token) {
            String value = token.getValue();
            return GrammarUtil.isOperator(value);
        }

        private VarDefineStatement parseVarDefine() {
            Token varType = tokens.get(curr);
            Token varName = needNext(TokenType.Words);
            return new VarDefineStatement(varType.getValue(), varName.getValue());
        }

        private Statement parseKeyword() {
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
            if ("new".equals(value)) {
                Expression expression = parseObjectNew();
                return new ExpStatement(expression);
            }
            return null;
        }

        private boolean isKeywords(Token token) {
            String value = token.getValue();
            return GrammarUtil.isCodeKeywords(value);
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
            parseExpression();
            needNext(TokenType.RParen);
            parseCodeBlock();
            return null;
        }

        private Statement parseFor() {
            needNext(TokenType.LParen);
            needNext();
            parseStatement();
            needNext(TokenType.Symbol, SEMICOLONS);
            // TODO 布尔表达式
            parseExpression();
            needNext(TokenType.Symbol, SEMICOLONS);
            parseStatement();
            needNext(TokenType.RParen);
            parseCodeBlock();
            return null;
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
            // TODO 支持 括号包裹的表达式
            // TODO 支持表达式后继续调用方法
            Token token = tokens.get(curr);
            if (token.getType() == TokenType.Words) {
                if ("new".equals(token.getValue())) {
                    // 创建对象
                    Expression expression = parseObjectNew();
                    if (isExpressionEnd()) {
                        return expression;
                    }
                    // 双目
                    return parseBinary(expression);
                }
                if (isNext(TokenType.LParen)) {
                    // 方法调用
                    Expression expression = parseMethodCall();
                    if (isExpressionEnd()) {
                        return expression;
                    }
                    // 双目
                    return parseBinary(expression);
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
                            if (isExpressionEnd()) {
                                return expression;
                            }
                            // 双目
                            return parseBinary(expression);
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
                throwIllegalToken(next.getValue());
                return null;
            }
            if (token.getType() == TokenType.Symbol) {
                Expression expression = parseUnaryOperate();
                if (isExpressionEnd()) {
                    return expression;
                }
                // 双目
                return parseBinary(expression);
            }
            if (token.getType() == TokenType.String) {
                Expression expression = new StringLiteral(token.getValue());
                if (isExpressionEnd()) {
                    return expression;
                }
                // 双目
                return parseBinary(expression);
            }
            if (token.getType() == TokenType.Number) {
                Expression expression = new NumberLiteral(token.getValue());
                if (isExpressionEnd()) {
                    return expression;
                }
                // 双目
                return parseBinary(expression);
            }
            throwIllegalToken(token.getValue());
            return null;
        }

        private boolean isCompare(String value) {
            return ">".equals(value)
                    || "<".equals(value)
                    || EQUAL.equals(value)
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
                // >=,<=
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
         * */
        private String parseOperator() {
            Token token = tokens.get(curr);
            String operator = token.getValue();
            if (!isOperator(token)) {
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
            return new MethodCall(calledMethod, expressions);
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
                    ;
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
            Token next = peekNext();
            return next != null && next.getType() == type && value.equals(next.getValue());
        }

        /**
         * 窥探下一个token，不移动指针
         */
        private Token peekNext() {
            int peekPoint = curr + 1;
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
}
