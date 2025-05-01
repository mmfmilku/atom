package org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.CodeBlockParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.IfStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

public class IfParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isCurr(TokenType.Words, "if");
    }

    @Override
    public IfStatement parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "if");
        iterator.needNext(TokenType.LParen);
        iterator.needNext();
        Expression condition = iterator.parseExpression();
        iterator.needNext(TokenType.RParen);
        iterator.needNext();
        CodeBlockParser codeBlockParser = iterator.getParser(CodeBlockParser.class);
        Statement trueStatement = codeBlockParser.parse(iterator);
        Statement falseStatement = null;
        while (iterator.isNext(TokenType.Words, "else")) {
            iterator.readNext();
            if (iterator.isNext(TokenType.Words, "if")) {
                iterator.readNext();
                falseStatement = this.parse(iterator);
            } else {
                iterator.needNext();
                falseStatement = codeBlockParser.parse(iterator);
                break;
            }
        }
        IfStatement ifStatement = new IfStatement(condition, trueStatement);
        ifStatement.setFalseStatement(falseStatement);
        return ifStatement;
    }

}
