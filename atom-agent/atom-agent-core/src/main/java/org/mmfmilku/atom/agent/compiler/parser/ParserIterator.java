package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.handle.ParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.CodeBlockParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.ExpressionParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.VarDefineParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.struct.GenericsParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Generics;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;
import org.mmfmilku.atom.exception.SystemException;
import org.mmfmilku.atom.util.ReflectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ParserIterator {

    public static String SEMICOLONS = ";";

    public static String COLON = ":";

    public static String COMMA = ",";

    public static String POINT = ".";

    public static String EQUAL = "=";

    List<Token> tokens;

    int curr = 0;

    Integer saveCurr = null;

    public static Statement EMPTY = new CodeBlock();

    /**
     * 管理所有parserHandle实现，以处理各parserHandle之间的循环依赖
     * */
    private Map<String, ParserHandle> parserHandleMap = new HashMap<>();

    public ParserIterator(List<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            throw new RuntimeException("tokens is null or empty");
        }
        this.tokens = tokens;
        try {
            initHandleMap();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Expression parseExpression() {
        ExpressionParser expressionParser = this.getParser(ExpressionParser.class);
        return expressionParser.parse(this);
    }

    public CodeBlock parseCodeBlock() {
        CodeBlockParser codeBlockParser = this.getParser(CodeBlockParser.class);
        return codeBlockParser.parse(this);
    }

    private void initHandleMap() throws IllegalAccessException, InstantiationException {
        List<String> scanClass1 = ReflectUtils.scanClass(
                "org.mmfmilku.atom.agent.compiler.parser.handle.code");
        List<String> scanClass2 = ReflectUtils.scanClass(
                "org.mmfmilku.atom.agent.compiler.parser.handle.struct");
        for (String className : scanClass1) {
            parserHandleMap.put(className,
                    (ParserHandle) ReflectUtils.forName(className).newInstance());
        }
        for (String className : scanClass2) {
            parserHandleMap.put(className,
                    (ParserHandle) ReflectUtils.forName(className).newInstance());
        }
    }

    public <T extends ParserHandle<? extends Node>> T getParser(Class<T> clazz) {
        return (T) parserHandleMap.get(clazz.getName());
    }

    /**
     * 获取如 xx.xx.xx 的字符
     * */
    public String parseWordsPoint() {
        Token token = getCurr();
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

    public boolean hasNext() {
        return curr < tokens.size() - 1;
    }

    public Token getCurr() {
        return tokens.get(curr);
    }

    public void beforeFirst() {
        this.curr = -1;
    }

    public void back() {
        this.curr--;
    }

    /**
     * 判断当前token，不移动指针
     */
    public boolean isCurr(TokenType type) {
        Token currToken = tokens.get(this.curr);
        return currToken != null && currToken.getType() == type;
    }

    /**
     * 判断当前token，不移动指针
     */
    public boolean isCurr(TokenType type, String value) {
        Token currToken = tokens.get(this.curr);
        return currToken != null && currToken.getType() == type && value.equals(currToken.getValue());
    }

    public boolean isLast() {
        return this.curr == this.tokens.size() - 1;
    }

    public Token checkCurr(TokenType type) {
        Token token = getCurr();
        if (token == null) {
            this.throwParserErr(type, type.getFixValue());
        }
        if (token.getType() != type) {
            this.throwParserErr(type, token.getType());
        }
        return token;
    }

    public void checkCurr(TokenType type, String value) {
        Token token = getCurr();
        if (token == null) {
            this.throwParserErr(type, value);
        }
        if (token.getType() != type) {
            this.throwParserErr(type, token.getType(), value, token.getValue());
        }
        if (!token.getValue().equals(value)) {
            this.throwParserErr(type, token.getType(), value, token.getValue());
        }
    }

    /**
     * 判断下一个token，不移动指针
     */
    public boolean isNext(TokenType type) {
        Token next = peekNext();
        return next != null && next.getType() == type;
    }

    /**
     * 判断下一个token，不移动指针
     */
    public boolean isNext(TokenType type, String value) {
        return isNext(1, type, value);
    }

    /**
     * 判断下n个token，不移动指针
     */
    public boolean isNext(int n, TokenType type) {
        return isNext(n, type, type.getFixValue());
    }

    /**
     * 判断下n个token，不移动指针
     */
    public boolean isNext(int n, TokenType type, String value) {
        Token next = peekNext(n);
        return next != null && next.getType() == type && value.equals(next.getValue());
    }

    /**
     * 窥探下一个token，不移动指针
     */
    public Token peekNext() {
        return peekNext(1);
    }

    /**
     * 窥探下一个token，不移动指针
     */
    public Token peekNext(int n) {
        int peekPoint = curr + n;
        if (peekPoint < tokens.size()) {
            return tokens.get(peekPoint);
        }
        return null;
    }

    /**
     * 读取至某个类型前，移动指针
     */
    public List<Token> readBefore(TokenType type) {
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
    public void readBefore(TokenType type, Consumer<Token> consumer) {
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
    public Token readNext() {
        curr++;
        if (curr < tokens.size()) {
            return tokens.get(curr);
        }
        return null;
    }

    /**
     * 需要的下一个token，移动指针
     */
    public Token needNext() {
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
    public Token needNext(TokenType type) {
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
    public Token needNext(TokenType type, String value) {
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

    public boolean isOperator(Token token) {
        String value = token.getValue();
        return GrammarUtil.isOperator(value);
    }

    public boolean isPlusMinus(String operator) {
        return "+".equals(operator) || "-".equals(operator);
    }

    public boolean isKeywords(Token token) {
        return TokenType.Words == token.getType()
                && GrammarUtil.isCodeKeywords(token.getValue());
    }

    /**
     * 解析方法传参 (e1,e2)
     * */
    public List<Expression> parameterPassing() {
        List<Expression> expressions = new ArrayList<>();
        if (isNext(TokenType.RParen)) {
            needNext();
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
     * 解析数组初始传参 {e1,e2}
     * */
    public List<Expression> parameterArrInitPassing() {
        List<Expression> expressions = new ArrayList<>();
        if (isNext(TokenType.RBrace)) {
            needNext();
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
        needNext(TokenType.RBrace);
        return expressions;
    }

    /**
     * 解析方法参数定义
     * */
    public List<VarDefineStatement> parameterDefine() {
        this.checkCurr(TokenType.LParen);
        List<VarDefineStatement> paramDefines = new ArrayList<>();
        if (isNext(TokenType.RParen)) {
            needNext();
            return paramDefines;
        }
        while (true) {
            needNext(TokenType.Words);
            VarDefineParser varDefineParser = getParser(VarDefineParser.class);
            VarDefineStatement varDefine = varDefineParser.parse(this);
            paramDefines.add(varDefine);
            if (!isNext(TokenType.Symbol, COMMA)) {
                break;
            }
            needNext();
        }
        needNext(TokenType.RParen);
        return paramDefines;
    }

    public boolean isCompare(String value) {
        return ">".equals(value)
                || "<".equals(value)
                || EQUAL.equals(value)
                || ("!".equals(value) && isNext(TokenType.Symbol, EQUAL))
                ;
    }

    public String getCompare() {
        Token token = getCurr();
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

    /**
     * 解析表达式中的操作符
     * +,-,*,/,&,|,^
     * &&,||
     * >=, <=, ==, !=
     * */
    public String parseOperator() {
        Token token = getCurr();
        String operator = token.getValue();
        if (!isOperator(token)) {
            if (isCompare(operator)) {
                return getCompare();
            }
            throwIllegalToken(token.getValue());
        }
        if ("&".equals(operator) || "|".equals(operator)) {
            if (isNext(TokenType.Symbol, operator)) {
                needNext();
                operator += operator;
            }
        }
        return operator;
    }

    public List<String> parseThrowList() {
        Token token = getCurr();
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

    /**
     * 允许泛形的位置，解析泛形并后移
     * */
    public Generics parseGenericsAndNext() {
        GenericsParser genericsParser = getParser(GenericsParser.class);
        if (genericsParser.match(this)) {
            Generics generics = genericsParser.parse(this);
             needNext();
            return generics;
        }
        return null;
    }

    public void throwIllegalToken(String value) {
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
