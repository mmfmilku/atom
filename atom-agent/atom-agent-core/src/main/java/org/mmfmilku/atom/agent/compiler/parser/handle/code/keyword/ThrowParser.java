package org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ReturnStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ThrowStatement;

/**
 * 解析 主动抛异常语句
 * */
public class ThrowParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isCurr(TokenType.Words, "throw");
    }

    @Override
    public Statement parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "throw");
        iterator.needNext();
        Expression expression = iterator.parseExpression();
        iterator.needNext(TokenType.Symbol, SEMICOLONS);
        return new ThrowStatement(expression);
    }

}
