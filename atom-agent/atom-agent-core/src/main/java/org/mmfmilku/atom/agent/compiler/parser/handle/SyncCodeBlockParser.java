package org.mmfmilku.atom.agent.compiler.parser.handle;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.SyncCodeBlock;

public class SyncCodeBlockParser implements ParserHandle {
    @Override
    public SyncCodeBlock parse(ParserDispatcher.ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "synchronized");
        // 解析同步对象
        iterator.needNext(TokenType.LParen);
        iterator.needNext();
        Expression expression = iterator.parseExpression();
        iterator.needNext(TokenType.RParen);
        // 解析临界区
        iterator.needNext(TokenType.LBrace);
        CodeBlock codeBlock = iterator.parseBlock();
        SyncCodeBlock syncCodeBlock = new SyncCodeBlock(codeBlock);
        syncCodeBlock.setSyncObject(expression);
        return syncCodeBlock;
    }

    @Override
    public int parseScope() {
        return HandleScope.assembly(HandleScope.IN_CODE_BLOCK);
    }
}
