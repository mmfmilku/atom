package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.handle.ParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.util.ReflectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserIterator {
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
        return helper.parseExpression();
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
    }

    public <T extends ParserHandle<? extends Node>> T getParser(Class<T> clazz) {
        return (T) parserHandleMap.get(clazz.getName());
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
}
