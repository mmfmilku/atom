package org.mmfmilku.atom.agent.compiler.parser.handle;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;

/**
 * 变量定义解析
 * */
public class VarDefineParser implements ParserHandle {

    @Override
    public VarDefineStatement parse(ParserDispatcher.ParserIterator iterator) {
        String varType = iterator.parseWordsPoint();
        Token varName = iterator.needNext(TokenType.Words);
        return new VarDefineStatement(varType, varName.getValue());
    }

    @Override
    public int parseScope() {
        return HandleScope.assembly(HandleScope.IN_CODE_BLOCK, HandleScope.IN_CLASS);
    }

}
