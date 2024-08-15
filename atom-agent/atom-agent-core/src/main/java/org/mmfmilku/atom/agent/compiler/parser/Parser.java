package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Package;

import java.util.ArrayList;
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

    public static final String SEMICOLONS = ";";

    public static final String COMMA = ",";

    private Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public void execute() {
        ParserHandle handle = new ParserHandle();
        handle.execute();
    }

    private class ParserHandle {
        List<Token> tokens;
        JavaFile javaFile;
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
            parseProgram();
            while (curr < tokens.size()) {
                dealToken = tokens.get(curr);
                parseProgram();
            }
        }

        private void parseProgram() {
            JavaFile javaFile = new JavaFile();
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
                        Class node = parseClass();
                        node.setModifier(modifier);
                        javaFile.getClassList().add(node);
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
                    // TODO 设置值
                    Annotation annotation = new Annotation();
                    String annotationName = token.getValue();
                    if (isNext(TokenType.LParen)) {
                        /*
                          移动两步至param
                          @annotation ( param )  ->  @annotation ( param )
                          ^                                        ^
                         * */
                        curr++;
                        curr++;
                        parameterPassing();
                        needNext(TokenType.RParen);
                    }
                    annotations.add(annotation);
                    curr++;
                } else {
                    break;
                }
            }
            return annotations;
        }

        /**
         * 解析方法传参
         * */
        private void parameterPassing() {
            readBefore(TokenType.RParen, token -> {
                // TODO，传参存在嵌套方法调用
            });
        }

        /**
         * 解析方法参数定义
         * */
        private void parameterDefine() {
            readBefore(TokenType.RParen, token -> {
                // TODO
            });
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
            // TODO 添加 import static xxx
            Token token = needNext(TokenType.Words);
            Token symbol = needNext(TokenType.Symbol);
            Import anImport = new Import();
            if (SEMICOLONS.equals(symbol.getValue())) {
                // import xx.xx.*;
                anImport.setValue(token.getValue() + symbol.getValue());
            } else {
                anImport.setValue(token.getValue());
                needNext(TokenType.Symbol, SEMICOLONS);
            }
            return anImport;
        }

        private Class parseClass() {
            Class clazz = new Class();

            Token className = needNext(TokenType.Words);
            clazz.setValue(className.getValue());

            needNext(TokenType.LBrace);

            List<Method> methods = new ArrayList<>();
            clazz.setMethods(methods);
            Token token;
            while ((token = readNext()) != null && token.getType() != TokenType.RBrace) {
                List<Annotation> annotations = getAnnotations();
                // TODO 解析成员变量
                // 先解析方法
                methods.add(parseMethod());
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
            Modifier modifier = getModifierAndNext();
            // todo 添加 static,final
            Token returnToken = tokens.get(curr);
            curr++;
            Token methodName = tokens.get(curr);
            needNext(TokenType.LParen);
            // TODO 方法参数
            parameterDefine();
            needNext(TokenType.RParen);
            if (isNext(TokenType.Words, "throws")) {
                curr++;
                // TODO 处理方法异常抛出
                readBefore(TokenType.LBrace, token -> {});
            }
            needNext(TokenType.LBrace);
            if (!isNext(TokenType.RBrace)) {
                curr++;
                // TODO 代码体
                CodeBlock codeBlock = parseCodeBlock();
            }
            needNext(TokenType.RBrace);

            Method method = new Method();
            return null;
        }

        private CodeBlock parseCodeBlock() {
            CodeBlock codeBlock = new CodeBlock();
            while (curr < tokens.size()) {
                Token token = tokens.get(curr);
                if (token.getType() == TokenType.Words) {
                    String value = token.getValue();
                    // TODO
                    if (isKeywords(token)) {
                        parseKeyword();
                        curr++;
                        continue;
                    }
                    if (isNext(TokenType.Words)) {
                        // 变量定义
                        continue;
                    }
                    if (isNext(TokenType.Symbol)) {
                        // TODO 泛形处理
                        // 变量赋值
                        continue;
                    }
                    if (isNext(TokenType.LParen)) {
                        // 方法调用
                        continue;
                    }
                    // if is keyword
                    // if is Class
                    // if is variable
                    // if is methodCall
                } else if (token.getType() == TokenType.Symbol) {
                    curr++;
                    // ; skip
                    // ++,-- operator
                    // others throw
                } else if (token.getType() == TokenType.LBrace) {
                    curr++;
                    CodeBlock innerBlock = parseCodeBlock();
                } else {
                    // throw
                }
            }
            return codeBlock;
        }

        private Node parseKeyword() {
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
            return null;
        }


        private boolean isKeywords(Token token) {
            String value = token.getValue();
            // TODO  do {} while() , synchronized, return
            return "for".equals(value) ||
                    "while".equals(value) ||
                    "if".equals(value);
        }

        private Node parseStatement() {
            if (true) {

            }
            return null;
        }

        private Node parseIf() {
            Token token = needNext();
            if (token.getType() == TokenType.LParen) {
                readNext();
                parseExpression();
                needNext(TokenType.RParen);
            } else {
                parseExpression();
            }
            // TODO
            return null;
        }

        /**
         * 解析表达式
         * */
        private void parseExpression() {
            Token token = tokens.get(curr);
            if (token.getType() == TokenType.Words) {
                // todo
                return;
            }
            if (token.getType() == TokenType.Symbol) {
                parseUnaryOperator();
                Token next = needNext(TokenType.Words);
                if (isNext(TokenType.LParen)) {
                    // TODO 方法调用
                }
                if (isNext(TokenType.RParen)) {
                    // TODO 单目表达式，直接返回
                    return;
                }
                if (isNext(TokenType.Symbol)) {
                    // TODO, 操作符 后跟表达式
                    // 包含 +,-,*,/,&,|,&&,||,...
                    return;
                }
                return;
            }
            // TODO

            printParsed();
            throw new RuntimeException("非法字符 " + dealToken.getValue());
        }

        /**
         * 单目运算符 --,++,!
         * */
        private Node parseUnaryOperator () {
            Token token = tokens.get(curr);
            String value = token.getValue();
            if ("!".equals(value)) {
                return null;
            }
            if ("-".equals(value)) {
                needNext(TokenType.Symbol, "-");
                return null;
            }
            if ("+".equals(value)) {
                needNext(TokenType.Symbol, "+");
                return null;
            }
            throwIllegalToken(value);
            return null;
        }

        /**
         * 双目运算符 =,+=,-=,*=,/=
         * */
        private Node parseBinaryOperator () {
            // TODO
            return null;
        }

        private Node parseWhile() {
            if (true) {

            }
            return null;
        }

        private Node parseFor() {
            if (true) {

            }
            return null;
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
            for (int i = 0; i < curr; i++) {
                parsed.append(tokens.get(i).showCode() + "\n");
            }
            System.out.println("当前已解析语法");
            System.out.println(parsed.toString());
        }

    }
}
