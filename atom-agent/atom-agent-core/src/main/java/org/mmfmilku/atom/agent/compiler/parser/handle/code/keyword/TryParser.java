package org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.CodeBlockParser;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.handle.code.VarDefineAssignParser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.TryStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;

import java.util.ArrayList;
import java.util.List;

public class TryParser implements CodeParserHandle {

    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isCurr(TokenType.Words, "try");
    }

    @Override
    public TryStatement parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "try");
        TryStatement tryStatement = new TryStatement();
        if (iterator.isNext(TokenType.LParen)) {
            // 解析try with resource中的变量定义
            iterator.needNext();
            iterator.needNext(TokenType.Words);
            List<VarDefineStatement> varDefines = new ArrayList<>();
            VarDefineAssignParser varDefineParser = new VarDefineAssignParser();
            // TODO 校验，必须是定义并赋值
            varDefines.add(varDefineParser.parse(iterator));
            while (iterator.isNext(TokenType.Symbol, SEMICOLONS)) {
                iterator.needNext();
                if (iterator.isNext(TokenType.RParen)) {
                    break;
                }
                iterator.needNext(TokenType.Words);
                varDefines.add(varDefineParser.parse(iterator));
            }
            iterator.needNext(TokenType.RParen);
            tryStatement.setAutoCloseDefines(varDefines);
        }

        // 解析try代码块
        iterator.needNext(TokenType.LBrace);
        CodeBlockParser codeBlockParser = iterator.getParser(CodeBlockParser.class);
        CodeBlock tryBody = codeBlockParser.parse(iterator);
        tryStatement.setTryBody(tryBody);

        while (iterator.isNext(TokenType.Words, "catch")) {
            // 解析catch捕获变量定义
            iterator.needNext();
            iterator.needNext(TokenType.LParen);

            List<String> throwableTypes = new ArrayList<>();
            iterator.needNext(TokenType.Words);
            String exceptionType = iterator.parseWordsPoint();

            throwableTypes.add(exceptionType);
            while (iterator.isNext(TokenType.Symbol, "|")) {
                iterator.needNext();
                throwableTypes.add(
                        iterator.needNext(TokenType.Words).getValue());
            }
            String varName = iterator.needNext(TokenType.Words).getValue();
            TryStatement.ThrowableCatch throwableCatch =
                    new TryStatement.ThrowableCatch(throwableTypes, varName);
            iterator.needNext(TokenType.RParen);

            // 解析catch代码块
            iterator.needNext(TokenType.LBrace);
            CodeBlock codeBlock = codeBlockParser.parse(iterator);

            tryStatement.getThrowableCatches().put(throwableCatch, codeBlock);
        }

        if (iterator.isNext(TokenType.Words, "finally")) {
            // 解析finally代码块
            iterator.needNext();
            iterator.needNext(TokenType.LBrace);
            tryStatement.setFinallyBody(codeBlockParser.parse(iterator));
        }
        return tryStatement;
    }

}
