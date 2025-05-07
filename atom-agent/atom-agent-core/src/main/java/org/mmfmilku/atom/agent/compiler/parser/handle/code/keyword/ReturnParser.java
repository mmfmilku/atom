package org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ReturnStatement;

/**
 * 解析return语句
 * */
public class ReturnParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isCurr(TokenType.Words, "return");
    }

    @Override
    public ReturnStatement parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "return");
        if (iterator.isNext(TokenType.Symbol, SEMICOLONS)) {
            // 直接return的情况
            iterator.needNext(TokenType.Symbol, SEMICOLONS);
            return new ReturnStatement();
        }
        // return具体的值
        iterator.needNext();
        Expression expression = iterator.parseExpression();
        iterator.needNext(TokenType.Symbol, SEMICOLONS);
        return new ReturnStatement(expression);
    }

}
