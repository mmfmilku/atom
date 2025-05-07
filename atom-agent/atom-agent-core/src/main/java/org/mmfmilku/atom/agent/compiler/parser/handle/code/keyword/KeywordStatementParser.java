package org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ExpStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

public class KeywordStatementParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isKeywords(iterator.getCurr());
    }

    @Override
    public Statement parse(ParserIterator iterator) {
        Token token = iterator.getCurr();
        String value = token.getValue();
        if ("if".equals(value)) {
            IfParser parser = iterator.getParser(IfParser.class);
            return parser.parse(iterator);
        }
        if ("for".equals(value)) {
            ForParser parser = iterator.getParser(ForParser.class);
            return parser.parse(iterator);
        }
        if ("while".equals(value)) {
            WhileParser parser = iterator.getParser(WhileParser.class);
            return parser.parse(iterator);
        }
        if ("do".equals(value)) {
            DoWhileParser parser = iterator.getParser(DoWhileParser.class);
            return parser.parse(iterator);
        }
        if ("new".equals(value)) {
            Expression expression = iterator.parseExpression();
            iterator.needNext(TokenType.Symbol, SEMICOLONS);
            return new ExpStatement(expression);
        }
        if ("return".equals(value)) {
            ReturnParser parser = iterator.getParser(ReturnParser.class);
            return parser.parse(iterator);
        }
        if ("try".equals(value)) {
            TryParser tryParser = iterator.getParser(TryParser.class);
            return tryParser.parse(iterator);
        }
        if ("synchronized".equals(value)) {
            SyncCodeBlockParser parser = iterator.getParser(SyncCodeBlockParser.class);
            return parser.parse(iterator);
        }
        if ("throw".equals(value)) {
            ThrowParser parser = iterator.getParser(ThrowParser.class);
            return parser.parse(iterator);
        }
        iterator.throwIllegalToken(value);
        return null;
    }
}
