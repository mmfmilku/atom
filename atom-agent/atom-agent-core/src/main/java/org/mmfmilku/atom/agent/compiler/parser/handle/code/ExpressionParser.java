package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.NumberLiteral;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.StringLiteral;

import java.util.List;

public class ExpressionParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return true;
    }

    @Override
    public Expression parse(ParserIterator iterator) {
        Token token = iterator.getCurr();
        if (token.getType() == TokenType.LParen) {
            // 左括号
            if (iterator.isNext(2, TokenType.RParen, TokenType.RParen.getFixValue())) {
                // 1.括号直接仅包裹一个单词，类型强转
                // TODO 泛形处理
                String typeName = iterator.needNext(TokenType.Words).getValue();
                iterator.needNext(TokenType.RParen);
                iterator.needNext();
                return new TypeCast(typeName, this.parse(iterator));
            }
            // 2.表达式括号包裹
            iterator.needNext();
            Expression expression = this.parse(iterator);
            iterator.needNext(TokenType.RParen);
            return parseToEnd(new PriorityExpression(expression), iterator);
        }
        if (token.getType() == TokenType.Words) {
            if ("new".equals(token.getValue())) {
                // 创建对象
                Expression expression = parseObjectNew(iterator);
                return parseToEnd(expression, iterator);
            }
            if (iterator.isNext(TokenType.LParen)) {
                // 方法调用
                Expression expression = parseMethodCall(iterator);
                return parseToEnd(expression, iterator);
            }
            Identifier identifier = new Identifier(token.getValue());
            if (isExpressionEnd(iterator)) {
                // 标识符
                return identifier;
            }
            Token next = iterator.needNext();
            String value = next.getValue();
            if (iterator.isOperator(next)) {
                if (iterator.isNext(TokenType.Symbol, value)) {
                    if (iterator.isPlusMinus(value)) {
                        // TODO support like: i ++ + ++ j
                        // 单目 i++
                        iterator.needNext();
                        Expression expression = new UnaryOperate(value + value, identifier, false);
                        return parseToEnd(expression, iterator);
                    }
                }
                // 双目
                iterator.back();
                return parseBinary(identifier, iterator);
            }
            if (iterator.isCompare(value) || EQUAL.equals(value)) {
                String compare = iterator.getCompare();
                iterator.needNext();
                Expression expression = iterator.parseExpression();
                return new BinaryOperate(identifier, compare, expression);
            }
            // todo 数组、泛形 待支持
            iterator.back();
            return parseToEnd(identifier, iterator);
        }
        if (token.getType() == TokenType.Symbol) {
            Expression expression = parseUnaryOperate(iterator);
            return parseToEnd(expression, iterator);
        }
        if (token.getType() == TokenType.String) {
            Expression expression = new StringLiteral(token.getValue());
            return parseToEnd(expression, iterator);
        }
        if (token.getType() == TokenType.Number) {
            Expression expression = new NumberLiteral(token.getValue());
            return parseToEnd(expression, iterator);
        }
        iterator.throwIllegalToken(token.getValue());
        return null;
    }

    private Expression parseObjectNew(ParserIterator iterator) {
        iterator.needNext(TokenType.Words);
        // 支持 new xx.xx.xx.C()
        String className = iterator.parseWordsPoint();
        ConstructorCall constructorCall = new ConstructorCall(className);
        iterator.needNext(TokenType.LParen);
        List<Expression> expressions = iterator.parameterPassing();
        constructorCall.setPassedParams(expressions);
        return constructorCall;
    }

    private Expression parseToEnd(Expression expression, ParserIterator iterator) {
        if (isExpressionEnd(iterator)) {
            return expression;
        }
        if (iterator.isNext(TokenType.Symbol, POINT)) {
            Expression callChain = parseCallChain(expression, iterator);
            return parseToEnd(callChain, iterator);
        }
        // 双目
        return parseBinary(expression, iterator);
    }

    private Expression parseCallChain(Expression first, ParserIterator iterator) {
        if (!iterator.isNext(TokenType.Symbol, POINT)) {
            return first;
        }
        iterator.needNext(TokenType.Symbol, POINT);
        Token token = iterator.needNext(TokenType.Words);
        if (iterator.isNext(TokenType.LParen)) {
            Expression next = parseMethodCall(iterator);
            return new CallChain(first, next);
        }
        Identifier next = new Identifier(token.getValue());
        Expression nextChain = parseCallChain(next, iterator);
        return new CallChain(first, nextChain);
    }

    private Expression parseMethodCall(ParserIterator iterator) {
        Token token = iterator.getCurr();
        String calledMethod = token.getValue();
        iterator.needNext(TokenType.LParen);
        List<Expression> expressions = iterator.parameterPassing();
        MethodCall methodCall = new MethodCall(calledMethod);
        methodCall.setPassedParams(expressions);
        return methodCall;
    }

    /**
     * 单目运算符 --,++,!
     * */
    private Expression parseUnaryOperate(ParserIterator iterator) {
        Token token = iterator.getCurr();
        String value = token.getValue();
        if ("!".equals(value)) {
            iterator.needNext();
            Expression expression = this.parse(iterator);
            return new NotOperate(expression);
        }
        if ("-".equals(value) || "+".equals(value)) {
            iterator.needNext(TokenType.Symbol, value);
            Token next = iterator.needNext(TokenType.Words);
            return new UnaryOperate(value + value,
                    new Identifier(next.getValue()));
        }
        iterator.throwIllegalToken(value);
        return null;
    }

    /**
     * 双目运算符
     * */
    private Expression parseBinary(Expression expression, ParserIterator iterator) {
        iterator.needNext();
        String operator = iterator.parseOperator();
        iterator.needNext();
        Expression right = this.parse(iterator);
        return new BinaryOperate(expression, operator, right);
    }

    private boolean isExpressionEnd(ParserIterator iterator) {
        return iterator.isNext(TokenType.RParen)
                || iterator.isNext(TokenType.RBrace)
                || iterator.isNext(TokenType.Symbol, SEMICOLONS)
                || iterator.isNext(TokenType.Symbol, COMMA)
                || iterator.isLast()
                ;
    }
}
