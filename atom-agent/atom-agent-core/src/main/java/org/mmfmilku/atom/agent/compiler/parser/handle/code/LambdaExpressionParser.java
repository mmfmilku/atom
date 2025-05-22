package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.LambdaExpression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

import java.util.ArrayList;
import java.util.List;

public class LambdaExpressionParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        if (iterator.isCurr(TokenType.LParen)) {
            return
                    // () ->
                    (
                        iterator.isNext(TokenType.RParen)
                        && iterator.isNext(2, TokenType.Symbol, "-")
                        && iterator.isNext(3, TokenType.RAngle)
                    )
                        ||
                    // (p) ->
                    (
                        iterator.isNext(TokenType.Words)
                        && iterator.isNext(2, TokenType.RParen)
                        && iterator.isNext(3, TokenType.Symbol, "-")
                        && iterator.isNext(4, TokenType.RAngle)
                    )
                        ||
                    // (p1, p2) ->
                    (
                        iterator.isNext(TokenType.Words)
                        && iterator.isNext(2, TokenType.Symbol, COMMA)
                    )
                    ;
        } else {
            // p ->
            return iterator.isCurr(TokenType.Words)
                    && iterator.isNext(TokenType.Symbol, "-")
                    && iterator.isNext(2, TokenType.RAngle)
                    ;
        }
    }

    @Override
    public LambdaExpression parse(ParserIterator iterator) {
        if (!match(iterator)) {
            iterator.throwIllegalToken(iterator.getCurr().getValue());
        }
        List<Identifier> inputs = new ArrayList<>();
        if (iterator.isCurr(TokenType.LParen)) {
            // (p1, p2)
            while (true) {
                inputs.add(new Identifier(iterator.needNext(TokenType.Words).getValue()));
                if (iterator.isNext(TokenType.RParen)) {
                    // 入参结束
                    iterator.needNext(TokenType.RParen);
                    break;
                } else {
                    // 解析逗号
                    iterator.needNext(TokenType.Symbol, COMMA);
                }
            }
        } else {
            // p
            iterator.checkCurr(TokenType.Words);
            inputs.add(new Identifier(iterator.getCurr().getValue()));
        }
        // 解析 ->
        iterator.needNext(TokenType.Symbol, "-");
        iterator.needNext(TokenType.RAngle);
        // 解析代码体
        CodeBlock codeBlock;
        if (iterator.isNext(TokenType.LBrace)) {
            iterator.needNext();
            codeBlock = iterator.getParser(CodeBlockParser.class).parse(iterator);
        } else {
            iterator.needNext();
            StatementLineParser parser = iterator.getParser(StatementLineParser.class);
            Statement parse = parser.parse(iterator);
            codeBlock = new CodeBlock();
            codeBlock.getStatements().add(parse);
        }
        return new LambdaExpression(inputs, codeBlock);
    }
}
