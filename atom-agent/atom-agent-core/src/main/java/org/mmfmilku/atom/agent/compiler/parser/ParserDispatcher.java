package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.handle.*;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.*;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword.*;
import org.mmfmilku.atom.agent.compiler.parser.handle.struct.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Package;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.AccessPrivilege;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.*;
import org.mmfmilku.atom.exception.SystemException;

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

    class ParserHelper implements ParserHandle {
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

        @Override
        public boolean match(ParserIterator iterator) {
            return true;
        }

        public JavaAST parse(ParserIterator iterator) {
            javaAST = new JavaAST();
            curr = 0;
            while (curr < tokens.size()) {
                parseProgram();
            }
            return javaAST;
        }

        @Override
        public int parseScope() {
            return 0;
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
                        PackageParser packageParser = new PackageParser();
                        Package node = packageParser.parse(iterator);
                        javaAST.setPackageNode(node);
                        curr++;
                        continue;
                    }
                    if ("import".equals(value)) {
                        ImportParser importParser = new ImportParser();
                        Import node = importParser.parse(iterator);
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
            AnnotationParser annotationParser = iterator.getParser(AnnotationParser.class);
            while (curr < tokens.size()) {
                Token token = tokens.get(curr);
                if (token.getValue().startsWith("@")) {
                    Annotation annotation = annotationParser.parse(iterator);
                    annotations.add(annotation);
                    curr++;
                } else {
                    break;
                }
            }
            return annotations;
        }

        /**
         * 获取如 xx.xx.xx 的字符
         * */
        String parseWordsPoint() {
            return iterator.parseWordsPoint();
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
                if (isCurr(TokenType.LBrace)) {
                    // 若为大括号，解析静态代码块
                    CodeBlock codeBlock = iterator.parseCodeBlock();
                    codeBlock.setModifier(modifier);
                    List<CodeBlock> staticBlocks = clazz.getStaticBlocks();
                    if (staticBlocks == null) {
                        staticBlocks = new ArrayList<>();
                        clazz.setStaticBlocks(staticBlocks);
                    }
                    staticBlocks.add(codeBlock);
                    continue;
                }
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
                        ConstructorParser constructorParser = iterator.getParser(ConstructorParser.class);
                        method = constructorParser.parse(iterator);
                        constructors.add(method);
                    } else {
                        // 解析方法
                        MethodParser methodParser = iterator.getParser(MethodParser.class);
                        method = methodParser.parse(iterator);
                        methods.add(method);
                    }

                    method.setModifier(modifier);
                    method.setAnnotations(annotations);
                } else {
                    readIdx();
                    // 解析成员变量
                    // TODO 成员注解
                    VarDefineAssignParser varDefineAssignParser = iterator.getParser(VarDefineAssignParser.class);
                    VarDefineStatement varDefine = varDefineAssignParser.parse(iterator);
                    needNext(TokenType.Symbol, SEMICOLONS);
                    Member member = new Member(modifier, varDefine);
                    members.add(member);
                }

            }
            if (token == null) {
                throw new RuntimeException("缺少" + TokenType.RBrace + "值 " + TokenType.RBrace.getFixValue());
            }
            return clazz;
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
         * 解析表达式
         * */
        private Expression parseExpression() {
            ExpressionParser parser = iterator.getParser(ExpressionParser.class);
            return parser.parse(iterator);
        }

        /**
         * 判断当前token，不移动指针
         */
        private boolean isCurr(TokenType type) {
            Token currToken = tokens.get(this.curr);
            return currToken != null && currToken.getType() == type;
        }

        /**
         * 判断当前token，不移动指针
         */
        boolean isCurr(TokenType type, String value) {
            Token currToken = tokens.get(this.curr);
            return currToken != null && currToken.getType() == type && value.equals(currToken.getValue());
        }

        /**
         * 判断下一个token，不移动指针
         */
        boolean isNext(TokenType type) {
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
        boolean isNext(int n, TokenType type, String value) {
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
        Token peekNext(int n) {
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
        Token readNext() {
            curr++;
            if (curr < tokens.size()) {
                return tokens.get(curr);
            }
            return null;
        }

        /**
         * 需要的下一个token，移动指针
         */
        Token needNext() {
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
        Token needNext(TokenType type) {
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
        Token needNext(TokenType type, String value) {
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

        void throwParserErr(TokenType needType, String needValue) {
            printParsed();
            throw new RuntimeException("缺少" + needType + "值 " + needValue);
        }

        void throwParserErr(TokenType needType, TokenType inputType) {
            throwParserErr(needType, inputType, needType.getFixValue(), inputType.getFixValue());
        }

        private void throwParserErr(TokenType needType, TokenType inputType,
                                    String needValue) {
            throwParserErr(needType, inputType, needValue, inputType.getFixValue());
        }

        void throwParserErr(TokenType needType, TokenType inputType,
                            String needValue, String inputValue) {
            printParsed();
            throw new RuntimeException("缺少" + needType + "值 " + needValue
                    + " 输入" + inputType + "值 " + inputValue);
        }

        void printParsed() {
            StringBuilder parsed = new StringBuilder();
            for (int i = 0; i < curr && i < tokens.size(); i++) {
                parsed.append(tokens.get(i).showCode() + "\n");
            }
            System.out.println("当前已解析语法");
            System.out.println(parsed.toString());
        }

        public void back() {
            this.curr--;
        }
    }

}
