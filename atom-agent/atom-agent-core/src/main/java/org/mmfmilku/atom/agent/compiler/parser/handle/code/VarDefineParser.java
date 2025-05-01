package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.handle.HandleScope;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;

/**
 * 变量仅定义解析
 * */
public class VarDefineParser implements CodeParserHandle {

    @Override
    public boolean match(ParserIterator iterator) {
        // TODO
        return false;
    }

    @Override
    public VarDefineStatement parse(ParserIterator iterator) {
        String varType = iterator.parseWordsPoint();
        Token varName = iterator.needNext(TokenType.Words);
        return new VarDefineStatement(varType, varName.getValue());
    }

    @Override
    public int parseScope() {
        return HandleScope.assembly(HandleScope.IN_CODE_BLOCK, HandleScope.IN_CLASS);
    }

}
