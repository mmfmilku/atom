package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.ArrayElement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.ArrayElementAssign;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.BinaryOperate;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.UnaryOperate;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ExpStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.VarAssign;

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
            if (
                    // 连续[]才认为是数组定义
                    (iterator.isNext(TokenType.Symbol, "[") && iterator.isNext(2, TokenType.Symbol, "]")) ||
                    iterator.isNext(TokenType.LAngle) ||
                    iterator.isNext(TokenType.Words)) {
                // 数组、泛形、连续字母，必定为变量定义
                // 读取下标
                iterator.readIdx();
                // 变量定义
                VarDefineAssignParser parser = iterator.getParser(VarDefineAssignParser.class);
                return parser.parse(iterator);
            }
            iterator.needNext();
            if (iterator.isCurr(TokenType.Symbol)) {
                String varName = wordsPoint;
                Statement expression = parseVarOperate(iterator, varName);
                if (expression != null) return expression;
            }
            if (iterator.isCurr(TokenType.LParen)) {
                // 表达式，方法调用开头
                iterator.readIdx();
                Expression expression = iterator.parseExpression();
                return new ExpStatement(expression);
            }
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

    /**
     * 变量操作
     * 变量赋值、变量运算
     * */
    private Statement parseVarOperate(ParserIterator iterator, String varName) {
        // 处理数组元素赋值的下标
        Expression elementIndexExp = null;
        if (iterator.isCurr(TokenType.Symbol, "[")) {
            iterator.needNext();
            elementIndexExp = iterator.parseExpression();
            iterator.needNext(TokenType.Symbol, "]");
            iterator.needNext();
        }
        if (iterator.isCurr(TokenType.Symbol, EQUAL)) {
            // 变量赋值
            // 指向等于号后面的字符
            iterator.needNext();
            Expression expression = iterator.parseExpression();
            return elementIndexExp == null ?
                    new ExpStatement(new VarAssign(varName, expression)) :
                    new ExpStatement(new ArrayElementAssign(
                            new ArrayElement(new Identifier(varName), elementIndexExp), expression));
        } else {
            Expression varExp = elementIndexExp == null ? new Identifier(varName) :
                    new ArrayElement(new Identifier(varName), elementIndexExp);
            Token next = iterator.getCurr();
            if (iterator.isOperator(next)) {
                String operator = next.getValue();
                if (iterator.isNext(TokenType.Symbol, EQUAL)) {
                    // 变量赋值 a += exp
                    iterator.needNext();
                    iterator.needNext();
                    Expression expression = iterator.parseExpression();
                    BinaryOperate binaryOperate = new BinaryOperate(varExp, operator, expression);
//                    return new ExpStatement(new VarAssign(varName, binaryOperate));
                    return elementIndexExp == null ?
                            new ExpStatement(new VarAssign(varName, binaryOperate)) :
                            new ExpStatement(new ArrayElementAssign(new ArrayElement(new Identifier(varName), elementIndexExp), binaryOperate));
                }
                // a++,a--
                if (!iterator.isPlusMinus(operator)) {
                    iterator.throwIllegalToken(operator);
                }
                iterator.needNext(TokenType.Symbol, operator);
                UnaryOperate unaryOperate = new UnaryOperate(
                        operator + operator,
                        // TODO 数组元素的单目表达式待支持 arr[0]++,arr[3]--
                        new Identifier(varName),
                        false
                );
                return new ExpStatement(unaryOperate);
            } else {
                // TODO 其他符号
                iterator.throwIllegalToken(next.getValue());
            }

        }
        return null;
    }
}
