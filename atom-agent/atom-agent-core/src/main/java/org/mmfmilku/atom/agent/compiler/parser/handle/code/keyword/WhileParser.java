package org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.CodeBlockParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.WhileStatement;

public class WhileParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isCurr(TokenType.Words, "while");
    }

    @Override
    public WhileStatement parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "while");
        iterator.needNext(TokenType.LParen);
        iterator.needNext();
        Expression condition = iterator.parseExpression();
        iterator.needNext(TokenType.RParen);
        iterator.needNext(TokenType.LBrace);
        CodeBlockParser parser = iterator.getParser(CodeBlockParser.class);
        CodeBlock loopBody = parser.parse(iterator);
        return new WhileStatement(condition, loopBody);
    }
}
