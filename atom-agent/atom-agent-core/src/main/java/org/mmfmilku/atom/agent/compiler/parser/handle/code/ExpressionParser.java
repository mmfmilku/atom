package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Generics;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.NumberLiteral;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.StringLiteral;

import java.util.ArrayList;
import java.util.List;

public class ExpressionParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return true;
    }

    @Override
    public Expression parse(ParserIterator iterator) {
        Expression expression = parseSingle(iterator);
        return parseToEnd(expression, iterator);
//        Token token = iterator.getCurr();
//        LambdaExpressionParser lambdaExpressionParser = iterator.getParser(LambdaExpressionParser.class);
//        if (token.getType() == TokenType.LParen) {
//            if (lambdaExpressionParser.match(iterator)) {
//                // lambda后不再跟表达式
//                return lambdaExpressionParser.parse(iterator);
//            }
//            // 左括号
//            if (iterator.isNext(2, TokenType.RParen, TokenType.RParen.getFixValue())) {
//                // 1.括号直接仅包裹一个单词，类型强转
//                // TODO 泛形处理
//                String typeName = iterator.needNext(TokenType.Words).getValue();
//                iterator.needNext(TokenType.RParen);
//                iterator.needNext();
//                return new TypeCast(typeName, this.parse(iterator));
//            }
//            // 2.表达式括号包裹
//            iterator.needNext();
//            Expression expression = this.parse(iterator);
//            iterator.needNext(TokenType.RParen);
//            return parseToEnd(new PriorityExpression(expression), iterator);
//        }
//        if (token.getType() == TokenType.Words) {
//            if (lambdaExpressionParser.match(iterator)) {
//                // lambda后不再跟表达式
//                return lambdaExpressionParser.parse(iterator);
//            }
//            MethodReferenceParser methodReferenceParser =
//                    iterator.getParser(MethodReferenceParser.class);
//            if (methodReferenceParser.match(iterator)) {
//                // 方法引用后不再跟表达式
//                return methodReferenceParser.parse(iterator);
//            }
//            if ("new".equals(token.getValue())) {
//                // 创建对象
//                Expression expression = parseObjectNew(iterator);
//                return parseToEnd(expression, iterator);
//            }
//            if (iterator.isNext(TokenType.LParen)) {
//                // 方法调用
//                Expression expression = parseMethodCall(iterator);
//                return parseToEnd(expression, iterator);
//            }
//            Identifier identifier = new Identifier(token.getValue());
//            if (isExpressionEnd(iterator)) {
//                // 标识符
//                return identifier;
//            }
//            Token next = iterator.needNext();
//            String value = next.getValue();
//            if (iterator.isOperator(next)) {
//                if (iterator.isNext(TokenType.Symbol, value)) {
//                    if (iterator.isPlusMinus(value)) {
//                        // TODO support like: i ++ + ++ j
//                        // 单目 i++
//                        iterator.needNext();
//                        Expression expression = new UnaryOperate(value + value, identifier, false);
//                        return parseToEnd(expression, iterator);
//                    }
//                }
//                // 双目
//                iterator.back();
//                return parseBinaryOperate(identifier, iterator);
//            }
//            if (iterator.isCompare(value) || EQUAL.equals(value)) {
//                String compare = iterator.getCompare();
//                iterator.needNext();
//                Expression expression = iterator.parseExpression();
//                return new BinaryOperate(identifier, compare, expression);
//            }
//            // todo 数组、泛形 待支持
//            iterator.back();
//            return parseToEnd(identifier, iterator);
//        }
//        if (token.getType() == TokenType.Symbol) {
//            Expression expression = parseUnaryOperate(iterator);
//            return parseToEnd(expression, iterator);
//        }
//        if (token.getType() == TokenType.String) {
//            Expression expression = new StringLiteral(token.getValue());
//            return parseToEnd(expression, iterator);
//        }
//        if (token.getType() == TokenType.Number) {
//            Expression expression = new NumberLiteral(token.getValue());
//            return parseToEnd(expression, iterator);
//        }
//        iterator.throwIllegalToken(token.getValue());
//        return null;
    }

    /**
     * 解析单个完整表达式，非贪婪解析
     * */
    private Expression parseSingle(ParserIterator iterator) {
        Token token = iterator.getCurr();
        LambdaExpressionParser lambdaExpressionParser = iterator.getParser(LambdaExpressionParser.class);
        if (token.getType() == TokenType.LParen) {
            if (lambdaExpressionParser.match(iterator)) {
                // lambda后不再跟表达式
                return lambdaExpressionParser.parse(iterator);
            }
            // 左括号
            Token peekNext3 = iterator.peekNext(3);
            if (iterator.isNext(TokenType.Words)
                    && iterator.isNext(2, TokenType.RParen)
                    && peekNext3 != null
                    && !POINT.equals(peekNext3.getValue())
                    && !"?".equals(peekNext3.getValue())
                    && !GrammarUtil.isOperator(peekNext3.getValue())
                    && !iterator.isCompare(peekNext3.getValue())
            ) {
                // 括号直接仅包裹一个单词，并且强转后无法衔接表达式（后跟 .?操作符比较符 ）
                // 则认为是类型转换
                // TODO 待支持 (com.xx.xx.Class) (
                // TODO 泛形处理
                iterator.needNext(TokenType.Words).getValue();
                String typeName = iterator.parseWordsPoint();
                iterator.needNext(TokenType.RParen);
                iterator.needNext();
                // 强转的为单表达式
                return new TypeCast(typeName, this.parseSingle(iterator));
            }
            // 2.表达式括号包裹
            iterator.needNext();
            // 括号包裹最高优先级，递归解析完整表达式
            Expression expression = this.parse(iterator);
            iterator.needNext(TokenType.RParen);
            return new PriorityExpression(expression);
        }
        if (token.getType() == TokenType.Words) {
            if (lambdaExpressionParser.match(iterator)) {
                // lambda后不再跟表达式
                return lambdaExpressionParser.parse(iterator);
            }
            MethodReferenceParser methodReferenceParser =
                    iterator.getParser(MethodReferenceParser.class);
            if (methodReferenceParser.match(iterator)) {
                // 方法引用后不再跟表达式
                return methodReferenceParser.parse(iterator);
            }
            if ("new".equals(token.getValue())) {
                // 创建对象
                return parseObjectNew(iterator);
            }
            if (iterator.isNext(TokenType.LParen)) {
                // 方法调用
                return parseMethodCall(iterator);
            }
            return new Identifier(token.getValue());
        }
        if (token.getType() == TokenType.Symbol) {
            // 单目
            return parseUnaryOperate(iterator);
        }
        if (token.getType() == TokenType.String) {
            return new StringLiteral(token.getValue());
        }
        if (token.getType() == TokenType.Number) {
            return new NumberLiteral(token.getValue());
        }
        iterator.throwIllegalToken(token.getValue());
        return null;
    }

    /**
     * 解析表达式，贪婪解析
     * */
    private Expression parseToEnd(Expression expression, ParserIterator iterator) {
        if (isExpressionEnd(iterator)) {
            return expression;
        }
        if (iterator.isNext(TokenType.Symbol, POINT)) {
            Expression callChain = parseCallChain(expression, iterator);
            return parseToEnd(callChain, iterator);
        }
        if (iterator.isNext(TokenType.Symbol, "?")) {
            // 三目
            Expression ternaryOperate = parseTernaryOperate(expression, iterator);
            return parseToEnd(ternaryOperate, iterator);
        }
        if (expression instanceof Identifier && iterator.isNext(TokenType.Symbol)) {
            Token peek = iterator.peekNext();
            String value = peek.getValue();
            if (iterator.isOperator(peek)) {
                String operate = peek.getValue();
                // 操作符连续出现
                if (iterator.isNext(2, TokenType.Symbol, operate)) {
                    // + 或 -
                    if (iterator.isPlusMinus(operate)) {
                        // 单目 i++ i--
                        iterator.needNext();
                        iterator.needNext();
                        Expression unaryOperate = new UnaryOperate(
                                operate + operate, (Identifier) expression, false);
                        return parseToEnd(unaryOperate, iterator);
                    }
                }
                // 其余情况未return，执行最后的 parseBinaryOperate
            } else if (iterator.isCompare(value) || EQUAL.equals(value)) {
                Expression left = expression;
                iterator.needNext();
                String compare = iterator.getCompare();
                iterator.needNext();
                Expression right = parseSingle(iterator);
                BinaryOperate binaryOperate = new BinaryOperate(left, compare, right);
                return parseToEnd(binaryOperate, iterator);
            }
        }
        // 双目
        Expression binaryOperate = parseBinaryOperate(expression, iterator);
        return parseToEnd(binaryOperate, iterator);
    }

    private boolean isExpressionEnd(ParserIterator iterator) {
        // 表达式结束判断
        //  )  }  ;  ,  :
        return iterator.isNext(TokenType.RParen)
                || iterator.isNext(TokenType.RBrace)
                || iterator.isNext(TokenType.Symbol, SEMICOLONS)
                || iterator.isNext(TokenType.Symbol, COMMA)
                || iterator.isNext(TokenType.Symbol, COLON)
                || iterator.isLast()
                ;
    }

    private boolean isEndValue(String value) {
        // 表达式结束判断
        //  )  }  ;  ,  :
        return TokenType.RParen.getFixValue().equals(value)
                || TokenType.RBrace.getFixValue().equals(value)
                || SEMICOLONS.equals(value)
                || COMMA.equals(value)
                || COLON.equals(value)
                ;
    }

    private Expression parseObjectNew(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "new");
        iterator.needNext(TokenType.Words);
        // 支持 new xx.xx.xx.C()
        String className = iterator.parseWordsPoint();
        iterator.needNext();
        // TODO 泛形
        Generics generics = iterator.parseGenericsAndNext();
        className += GrammarUtil.emptyWrap(generics);
        ConstructorCall constructorCall = new ConstructorCall(className);
        if (iterator.isCurr(TokenType.Symbol, "[")) {
            constructorCall.setArr(true);
            // TODO 数组定义保存
            if (iterator.isNext(TokenType.Number)) {
                // 初始化数组大小
                Token arrSize = iterator.needNext(TokenType.Number);
                constructorCall.setArrSize(arrSize.getValue());
            }
            iterator.needNext(TokenType.Symbol, "]");
            if (iterator.isNext(TokenType.LBrace)) {
                // 数组定义同时赋予初始值
                // String arr = new String[]{"a", "b", "c"}
                // TODO 保存
                iterator.needNext();
                List<Expression> expressions = iterator.parameterArrInitPassing();
                constructorCall.setPassedParams(expressions);
            } else {
                // TODO 防止NPE
                constructorCall.setPassedParams(new ArrayList<>());
            }
        } else {
            // 调用构造方法
            iterator.checkCurr(TokenType.LParen);
            List<Expression> expressions = iterator.parameterPassing();
            constructorCall.setPassedParams(expressions);
        }
        return constructorCall;
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
    private Expression parseBinaryOperate(Expression expression, ParserIterator iterator) {
        iterator.needNext();
        String operator = iterator.parseOperator();
        iterator.needNext();
        Expression right = this.parseSingle(iterator);
        return new BinaryOperate(expression, operator, right);
    }

    private Expression parseTernaryOperate(Expression boolExp, ParserIterator iterator) {
        iterator.needNext(TokenType.Symbol, "?");
        iterator.needNext();
        Expression trueExp = parse(iterator);
        iterator.needNext(TokenType.Symbol, COLON);
        iterator.needNext();
        Expression falseExp = parse(iterator);
        return new TernaryOperate(boolExp, trueExp, falseExp);
    }

}
