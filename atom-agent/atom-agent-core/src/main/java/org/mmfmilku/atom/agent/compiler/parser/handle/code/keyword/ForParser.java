package org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.CodeBlockParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.StatementLineParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.*;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;
import org.mmfmilku.atom.util.AssertUtil;

public class ForParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isCurr(TokenType.Words, "for");
    }

    @Override
    public LoopStatement parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "for");
        iterator.needNext(TokenType.LParen);
        iterator.needNext();
        // parseStatement已经包含读取分号
        StatementLineParser statementLineParser = iterator.getParser(StatementLineParser.class);
        Statement beforeStatement = statementLineParser.parse(iterator);
        LoopStatement loopStatement;
        if (iterator.isNext(TokenType.Symbol, SEMICOLONS)) {
            // 普通for循环
            iterator.needNext();
            iterator.needNext();
            Expression loopCondition = iterator.parseExpression();
            iterator.needNext(TokenType.Symbol, SEMICOLONS);
            Statement afterStatement;
            if (iterator.isNext(TokenType.RParen)) {
                afterStatement = ParserIterator.EMPTY;
            } else {
                iterator.needNext();
                afterStatement = statementLineParser.parse(iterator);
            }
            loopStatement = new ForStatement(beforeStatement, afterStatement, loopCondition);
        } else {
            iterator.needNext(TokenType.Symbol, COLON);
            Token token = iterator.needNext(TokenType.Words);
            AssertUtil.isTrue(beforeStatement instanceof VarDefineStatement,
                    "is not var define:" + beforeStatement.getStatementSource());
            loopStatement = new EnhanceForStatement((VarDefineStatement) beforeStatement,
                    new Identifier(token.getValue()));
        }
        iterator.needNext(TokenType.RParen);
        iterator.needNext(TokenType.LBrace);
        CodeBlockParser codeBlockParser = iterator.getParser(CodeBlockParser.class);
        CodeBlock loopBody = codeBlockParser.parse(iterator);
        loopStatement.setLoopBody(loopBody);
        return loopStatement;
    }

}
