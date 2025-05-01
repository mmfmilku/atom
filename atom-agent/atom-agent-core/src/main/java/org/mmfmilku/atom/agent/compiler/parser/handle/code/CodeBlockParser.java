package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.handle.HandleScope;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

public class CodeBlockParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return true;
    }

    @Override
    public CodeBlock parse(ParserIterator iterator) {
        CodeBlock codeBlock = new CodeBlock();
        Token token = iterator.getCurr();
        StatementParser parser = iterator.getParser(StatementParser.class);
        if (token.getType() != TokenType.LBrace) {
            Statement statement = parser.parse(iterator);
            codeBlock.getStatements().add(statement);
            return codeBlock;
        }
        // 跳过 {
        while (iterator.needNext().getType() != TokenType.RBrace) {
            Statement statement = parser.parse(iterator);
            codeBlock.getStatements().add(statement);
        }
        return codeBlock;
    }

    @Override
    public int parseScope() {
        return HandleScope.assembly(HandleScope.IN_METHOD, HandleScope.IN_CODE_BLOCK);
    }
}
