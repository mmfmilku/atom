package org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.CodeBlockParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.SyncCodeBlock;

public class SyncCodeBlockParser implements CodeParserHandle {

    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isCurr(TokenType.Words, "synchronized");
    }

    @Override
    public SyncCodeBlock parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "synchronized");
        // 解析同步对象
        iterator.needNext(TokenType.LParen);
        iterator.needNext();
        Expression expression = iterator.parseExpression();
        iterator.needNext(TokenType.RParen);
        // 解析临界区
        iterator.needNext(TokenType.LBrace);
        CodeBlockParser parser = iterator.getParser(CodeBlockParser.class);
        CodeBlock codeBlock = parser.parse(iterator);
        SyncCodeBlock syncCodeBlock = new SyncCodeBlock(codeBlock);
        syncCodeBlock.setSyncObject(expression);
        return syncCodeBlock;
    }

}
