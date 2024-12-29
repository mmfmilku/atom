package org.mmfmilku.atom.agent.compiler.parser.handle;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.TryStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;

import java.util.ArrayList;
import java.util.List;

public class TryParser implements ParserHandle {

    @Override
    public TryStatement parse(ParserDispatcher.ParserIterator iterator) {
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
        CodeBlock tryBody = iterator.parseBlock();
        tryStatement.setTryBody(tryBody);

        while (iterator.isNext(TokenType.Words, "catch")) {
            // 解析catch捕获变量定义
            iterator.needNext();
            iterator.needNext(TokenType.LParen);

            List<String> throwableTypes = new ArrayList<>();
            Token throwType = iterator.needNext(TokenType.Words);
            throwableTypes.add(throwType.getValue());
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
            CodeBlock codeBlock = iterator.parseBlock();

            tryStatement.getThrowableCatches().put(throwableCatch, codeBlock);
        }

        if (iterator.isNext(TokenType.Words, "finally")) {
            // 解析finally代码块
            iterator.needNext();
            iterator.needNext(TokenType.LBrace);
            tryStatement.setFinallyBody(iterator.parseBlock());
        }
        return tryStatement;
    }

    @Override
    public int parseScope() {
        return HandleScope.assembly(HandleScope.IN_CODE_BLOCK);
    }
}
