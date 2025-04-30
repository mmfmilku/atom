package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.handle.HandleScope;
import org.mmfmilku.atom.agent.compiler.parser.handle.ParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.ReturnStatement;

/**
 * 解析return语句
 * */
public class ReturnParser implements ParserHandle {
    @Override
    public boolean match(ParserDispatcher.ParserIterator iterator) {
        return iterator.isCurr(TokenType.Words, "return");
    }

    @Override
    public ReturnStatement parse(ParserDispatcher.ParserIterator iterator) {
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

    @Override
    public int parseScope() {
        return HandleScope.assembly(HandleScope.IN_CODE_BLOCK);
    }
}
