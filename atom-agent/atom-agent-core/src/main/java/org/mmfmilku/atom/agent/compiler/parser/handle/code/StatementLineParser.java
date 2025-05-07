package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.BinaryOperate;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.UnaryOperate;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ExpStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarAssignStatement;

/**
 * 解析一般单行语句，不包含结束符 ;
 * */
public class StatementLineParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return true;
    }

    @Override
    public Statement parse(ParserIterator iterator) {
        Token token = iterator.getCurr();
        if (token.getType() == TokenType.Words) {
            iterator.saveIdx();
            String wordsPoint = iterator.parseWordsPoint();
            if (iterator.isNext(TokenType.Words)) {
                iterator.readIdx();
                // 变量定义
                VarDefineAssignParser parser = iterator.getParser(VarDefineAssignParser.class);
                return parser.parse(iterator);
            }
            if (iterator.isNext(TokenType.Symbol)) {
                String varName = wordsPoint;
                if (iterator.isNext(TokenType.Symbol, EQUAL)) {
                    // 变量赋值
                    // 指向等于号后面的字符
                    iterator.needNext();
                    iterator.needNext();
                    Expression expression = iterator.parseExpression();
                    return new VarAssignStatement(varName, expression);
                } else if (iterator.isNext(TokenType.LParen)) {
                    // 下标回溯
                    iterator.readIdx();
                    // 链式调用表达式语句，方法调用
                    Expression expression = iterator.parseExpression();
                    return new ExpStatement(expression);
                } else {
                    Token next = iterator.needNext();
                    if (iterator.isOperator(next)) {
                        String operator = next.getValue();
                        if (iterator.isNext(TokenType.Symbol, EQUAL)) {
                            // 变量赋值 a += exp
                            iterator.needNext();
                            iterator.needNext();
                            Expression expression = iterator.parseExpression();
                            BinaryOperate binaryOperate = new BinaryOperate(new Identifier(varName), operator, expression);
                            return new VarAssignStatement(varName, binaryOperate);
                        }
                        // a++,a--
                        if (!iterator.isPlusMinus(operator)) {
                            iterator.throwIllegalToken(operator);
                        }
                        iterator.needNext(TokenType.Symbol, operator);
                        UnaryOperate unaryOperate = new UnaryOperate(
                                operator + operator,
                                new Identifier(varName),
                                false
                        );
                        return new ExpStatement(unaryOperate);
                    } else {
                        // TODO 其他符号
                        iterator.throwIllegalToken(token.getValue());
                    }

                }
            }
            if (iterator.isNext(TokenType.LParen)) {
                // 表达式，方法调用开头
                iterator.readIdx();
                Expression expression = iterator.parseExpression();
                return new ExpStatement(expression);
            }
            // TODO 数组、泛形解析
            iterator.throwIllegalToken(token.getValue());
        } else if (token.getType() == TokenType.Symbol
                || token.getType() == TokenType.Number
                || token.getType() == TokenType.String
                || token.getType() == TokenType.Character) {
            // ++,-- operator
            // 字面量
            // others throw
            Expression expression = iterator.parseExpression();
            return new ExpStatement(expression);
        } else {
            // TODO lambda表达式
            iterator.throwIllegalToken(token.getValue());
        }
        return ParserIterator.EMPTY;
    }
}
