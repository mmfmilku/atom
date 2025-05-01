package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword.KeywordStatementParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

/**
 * 解析复杂嵌套语句，包含结束符 ;
 * */
public class StatementParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return true;
    }

    @Override
    public Statement parse(ParserIterator iterator) {
        Token token = iterator.getCurr();
        if (SEMICOLONS.equals(token.getValue())) {
            // ; skip
            // 空语句
            return ParserIterator.EMPTY;
        }
        if (iterator.isKeywords(token)) {
            // 关键字语句
            KeywordStatementParser parser = iterator.getParser(KeywordStatementParser.class);
            return parser.parse(iterator);
        }
        if (token.getType() == TokenType.LBrace) {
            CodeBlockParser codeBlockParser = iterator.getParser(CodeBlockParser.class);
            // 代码块
            return codeBlockParser.parse(iterator);
        }
        StatementLineParser statementLineParser = iterator.getParser(StatementLineParser.class);
        Statement statement = statementLineParser.parse(iterator);
        iterator.needNext(TokenType.Symbol, SEMICOLONS);
        return statement;
    }
}
