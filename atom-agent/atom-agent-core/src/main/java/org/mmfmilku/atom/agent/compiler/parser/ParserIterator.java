package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.handle.ParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.CodeBlockParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.ExpressionParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.VarDefineParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;
import org.mmfmilku.atom.util.ReflectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserIterator {

    String SEMICOLONS = ";";

    String COLON = ":";

    String COMMA = ",";

    String POINT = ".";

    String EQUAL = "=";

    private ParserDispatcher.ParserHelper helper;

    public static Statement EMPTY = new CodeBlock();

    /**
     * 管理所有parserHandle实现，以处理各parserHandle之间的循环依赖
     * */
    private Map<String, ParserHandle> parserHandleMap = new HashMap<>();

    public ParserIterator(ParserDispatcher.ParserHelper helper) {
        this.helper = helper;
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
            helper.throwParserErr(TokenType.Words, token.getType());
        }
        StringBuilder value = new StringBuilder(token.getValue());
        while (isNext(TokenType.Symbol, POINT)) {
            needNext(TokenType.Symbol, POINT);
            Token next = needNext(TokenType.Words);
            value.append(POINT).append(next.getValue());
        }
        return value.toString();
    }

    public Token getCurr() {
        return helper.tokens.get(helper.curr);
    }

    public void back() {
        helper.back();
    }

    public boolean isCurr(TokenType type, String value) {
        return helper.isCurr(type, value);
    }

    public boolean isLast() {
        return helper.curr == helper.tokens.size() - 1;
    }

    public void checkCurr(TokenType type) {
        Token token = getCurr();
        if (token == null) {
            helper.throwParserErr(type, type.getFixValue());
        }
        if (token.getType() != type) {
            helper.throwParserErr(type, token.getType());
        }
    }

    public void checkCurr(TokenType type, String value) {
        Token token = getCurr();
        if (token == null) {
            helper.throwParserErr(type, value);
        }
        if (token.getType() != type) {
            helper.throwParserErr(type, token.getType(), value, token.getValue());
        }
        if (!token.getValue().equals(value)) {
            helper.throwParserErr(type, token.getType(), value, token.getValue());
        }
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

    /**
     * 存档指针，仅保存一次的
     * */
    public void saveIdx() {
        helper.saveIdx();
    }

    /**
     * 读取指针，读取前需要保存，仅支持读取一次
     * */
    public void readIdx() {
        helper.readIdx();
    }

    public boolean isOperator(Token token) {
        String value = token.getValue();
        return GrammarUtil.isOperator(value);
    }

    public boolean isPlusMinus(String operator) {
        return "+".equals(operator) || "-".equals(operator);
    }

    public void throwIllegalToken(String value) {
        helper.printParsed();
        throw new RuntimeException("非法字符 " + value);
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
     * 解析方法参数定义
     * */
    public List<VarDefineStatement> parameterDefine() {
        List<VarDefineStatement> paramDefines = new ArrayList<>();
        if (isNext(TokenType.RParen)) {
            needNext();
            return paramDefines;
        }
        while (true) {
            needNext();
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
}
