package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.handle.*;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.*;
import org.mmfmilku.atom.agent.compiler.parser.handle.struct.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Package;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.AccessPrivilege;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;

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
public class ParserDispatcher {

    private Lexer lexer;

    public ParserDispatcher(Lexer lexer) {
        this.lexer = lexer;
    }

    public JavaAST execute() {
        ParserHelper handle = new ParserHelper();
        return handle.parse();
    }

    public Expression getExpression() {
        ParserHelper handle = new ParserHelper();
        return handle.getExpression();
    }

    public ParserAssembly newAssembly() {
        return new ParserAssembly();
    }

    /**
     * 语法组合
     * */
    public class ParserAssembly {

        private ParserIterator iterator;

        private List<AssemblyUnit> assemblyUnits = new ArrayList<>();

        public ParserAssembly() {
            List<Token> tokens = lexer.getTokens()
                    .stream()
                    .filter(token -> token.getType() != TokenType.BlockComment && token.getType() != TokenType.Comment)
                    .collect(Collectors.toList());
            iterator = new ParserIterator(tokens);
        }

        public <T extends ParserHandle<U>, U extends Node> void registry(
                java.lang.Class<T> clazz, Consumer<U> nodeAccept) {
            registry(clazz, nodeAccept, true);
        }

        public <T extends ParserHandle<U>, U extends Node> void registry(
                java.lang.Class<T> clazz, Consumer<U> nodeAccept, boolean canAbsent) {
            assemblyUnits.add(new AssemblyUnit<>(iterator.getParser(clazz), nodeAccept, canAbsent, false));
        }

        public <T extends ParserHandle<U>, U extends Node> void registryList(
                java.lang.Class<T> clazz, Consumer<List<U>> nodeAccept) {
            registryList(clazz, nodeAccept, true);
        }

        public <T extends ParserHandle<U>, U extends Node> void registryList(
                java.lang.Class<T> clazz, Consumer<List<U>> nodeAccept, boolean canAbsent) {
            assemblyUnits.add(new AssemblyUnit<>(iterator.getParser(clazz), nodeAccept, canAbsent, true));
        }

        public void parse() {
            iterator.beforeFirst();
            for (AssemblyUnit assemblyUnit : assemblyUnits) {
                // 语法非必要，并且是最后一位，跳过
                if (assemblyUnit.canAbsent && iterator.isLast()) {
                    continue;
                }
                iterator.needNext();
                ParserHandle handle = assemblyUnit.parserHandle;
                if (assemblyUnit.loop) {
                    // 一条都匹配不到
                    if (!handle.match(iterator)) {
                        if (assemblyUnit.canAbsent) {
                            // 非必要，回退，用于后续语法解析
                            iterator.back();
                            continue;
                        } else {
                            iterator.throwIllegalToken(iterator.getCurr().getValue());
                        }
                    }
                    // 至少匹配一条
                    List<Node> nodes = new ArrayList<>();
                    while (handle.match(iterator)) {
                        Node parse = handle.parse(iterator);
                        nodes.add(parse);
                        // 判断是否继续下一条
                        if (iterator.hasNext()) {
                            iterator.needNext();
                            if (!handle.match(iterator)) {
                                // 下一条不匹配
                                iterator.back();
                                break;
                            }
                        } else {
                            // 解析结束
                            break;
                        }
                    }
                    assemblyUnit.nodeAccept.accept(nodes);
                } else {
                    Node parse = handle.parse(iterator);
                    assemblyUnit.nodeAccept.accept(parse);
                }
            }
        }

    }

    private static class AssemblyUnit<T extends Node> {
        ParserHandle<T> parserHandle;

        Consumer<?> nodeAccept;

        boolean canAbsent;

        boolean loop;

        AssemblyUnit(ParserHandle<T> parserHandle, Consumer<?> nodeAccept, boolean canAbsent, boolean loop) {
            this.parserHandle = parserHandle;
            this.nodeAccept = nodeAccept;
            this.canAbsent = canAbsent;
            this.loop = loop;
        }
    }

    /**
     * 解析java文件
     * */
    private class ParserHelper {
        JavaAST javaAST;
        ParserIterator iterator;

        private ParserHelper() {
            List<Token> tokens = lexer.getTokens()
                    .stream()
                    .filter(token -> token.getType() != TokenType.BlockComment && token.getType() != TokenType.Comment)
                    .collect(Collectors.toList());
            iterator = new ParserIterator(tokens);
        }

        private JavaAST parse() {
            javaAST = new JavaAST();
            while (iterator.hasNext()) {
                parseProgram();
            }
            javaAST.buildLinkedNode();
            return javaAST;
        }

        private Expression getExpression() {
            return parseExpression();
        }

        private void parseProgram() {
            iterator.beforeFirst();
            while (iterator.hasNext()) {
                Token dealToken = iterator.needNext();
                if (dealToken.getType() == TokenType.Words) {
                    String value = dealToken.getValue();
                    if ("package".equals(dealToken.getValue())) {
                        PackageParser packageParser = new PackageParser();
                        Package node = packageParser.parse(iterator);
                        javaAST.setPackageNode(node);
                        continue;
                    }
                    if ("import".equals(value)) {
                        ImportParser importParser = new ImportParser();
                        Import node = importParser.parse(iterator);
                        javaAST.getImports().add(node);
                        continue;
                    }
                    List<Annotation> annotations = getAnnotations();
                    // TODO final,abstract 关键字待支持
                    Modifier modifier = parseModifierAndNext();
                    dealToken = iterator.getCurr();
                    value = dealToken.getValue();
                    if ("class".equals(value)) {
                        Class clazz = parseClass();
                        clazz.setModifier(modifier);
                        clazz.setAnnotations(annotations);
                        clazz.setClassFullName(javaAST.getPackageNode().getValue()
                                + "." + clazz.getClassName());
                        javaAST.getClassList().add(clazz);
                        continue;
                    }
                }
                iterator.throwIllegalToken(dealToken.getValue());
            }
        }

        /**
         * 解析注解，并指向下一位
         */
        private List<Annotation> getAnnotations() {
            List<Annotation> annotations = new ArrayList<>();
            AnnotationParser annotationParser = iterator.getParser(AnnotationParser.class);
            Token token = iterator.getCurr();
            while (token.getValue().startsWith("@")) {
                Annotation annotation = annotationParser.parse(iterator);
                annotations.add(annotation);
                token = iterator.needNext();
            }
            return annotations;
        }

        /**
         * 获取如 xx.xx.xx 的字符
         */
        String parseWordsPoint() {
            return iterator.parseWordsPoint();
        }

        private Class parseClass() {

            Token className = iterator.needNext(TokenType.Words);
            Class clazz = new Class(className.getValue());

            iterator.needNext();
            clazz.setGenerics(iterator.parseGenericsAndNext());

            if (iterator.isCurr(TokenType.Words)) {
                Token curr = iterator.getCurr();
                if ("extends".equals(curr.getValue())) {
                    iterator.needNext(TokenType.Words);
                    clazz.setSuperClass(parseWordsPoint());
                    iterator.needNext();
                    // TODO 继承类，泛形保存
                    Generics generics = iterator.parseGenericsAndNext();
                    if (iterator.isCurr(TokenType.Words, "implements")) {
                        parseImplementsAndNext(clazz);
                    }
                } else if ("implements".equals(curr.getValue())) {
                    parseImplementsAndNext(clazz);
                }
            }
            // 花括号，解析类内容
            iterator.checkCurr(TokenType.LBrace);
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
            while ((token = iterator.readNext()) != null && token.getType() != TokenType.RBrace) {
                // 解析注解
                List<Annotation> annotations = getAnnotations();
                // 解析修饰符 如：public static synchronized
                Modifier modifier = parseModifierAndNext();
                if (iterator.isCurr(TokenType.LBrace)) {
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
                // 泛形定义
                Generics methodGenerics = iterator.parseGenericsAndNext();
                iterator.saveIdx();
                // 判断是成员变量还是方法或构造器
                // 1.解析 parseWordsPoint 前存档，因为解析构造器和方法时会再次执行parseWordsPoint
                // 2.调用 parseWordsPoint 后再判断是因为 如 com.xx.xxx 会影响判断
                parseWordsPoint();
                if (iterator.isNext(TokenType.LParen) ||
                        iterator.isNext(2, TokenType.LParen, TokenType.LParen.getFixValue())) {
                    iterator.readIdx();
                    // 后一位或后两位是括号，则为方法定义
                    // TODO 抽象方法
                    Method method;
                    if (iterator.isCurr(TokenType.Words, className.getValue())) {
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

                    method.setGenerics(methodGenerics);
                    method.setModifier(modifier);
                    method.setAnnotations(annotations);
                } else {
                    iterator.readIdx();
                    // 解析成员变量
                    // TODO 成员注解
                    VarDefineAssignParser varDefineAssignParser = iterator.getParser(VarDefineAssignParser.class);
                    VarDefineStatement varDefine = varDefineAssignParser.parse(iterator);
                    iterator.needNext(TokenType.Symbol, ParserIterator.SEMICOLONS);
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

            for (String value = iterator.getCurr().getValue(); modifier.accept(value); ) {
                // 匹配到修饰符，指针加一
                value = iterator.needNext().getValue();
            }

            return modifier;
        }

        private AccessPrivilege getAccessPrivilegeAndNext() {
            AccessPrivilege accessPrivilege = AccessPrivilege.of(iterator.getCurr());
            if (accessPrivilege == null) {
                accessPrivilege = AccessPrivilege.DEFAULT;
            } else {
                iterator.needNext();
            }
            return accessPrivilege;
        }

        private void parseImplementsAndNext(Class clazz) {
            List<String> implementsList = new ArrayList<>();
            iterator.needNext(TokenType.Words);
            implementsList.add(parseWordsPoint());
            iterator.needNext();
            // TODO 实现接口，泛形保存
            Generics generics = iterator.parseGenericsAndNext();
            while (iterator.isCurr(TokenType.Symbol, ParserIterator.COMMA)) {
                iterator.needNext(TokenType.Words);
                implementsList.add(parseWordsPoint());
                iterator.needNext();
                // TODO 实现接口，泛形保存
                generics = iterator.parseGenericsAndNext();
            }
            clazz.setImplementClasses(implementsList);
        }

        /**
         * 解析表达式
         */
        private Expression parseExpression() {
            ExpressionParser parser = iterator.getParser(ExpressionParser.class);
            return parser.parse(iterator);
        }


    }

}
