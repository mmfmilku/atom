package org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.CodeBlockParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.DoWhileStatement;

public class DoWhileParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return false;
    }

    @Override
    public DoWhileStatement parse(ParserIterator iterator) {
        iterator.needNext(TokenType.LBrace);
        CodeBlockParser parser = iterator.getParser(CodeBlockParser.class);
        CodeBlock loopBody = parser.parse(iterator);
        iterator.needNext(TokenType.Words, "while");
        iterator.needNext(TokenType.LParen);
        iterator.needNext();
        Expression condition = iterator.parseExpression();
        iterator.needNext(TokenType.RParen);
        iterator.needNext(TokenType.Symbol, SEMICOLONS);
        return new DoWhileStatement(condition, loopBody);
    }
}
