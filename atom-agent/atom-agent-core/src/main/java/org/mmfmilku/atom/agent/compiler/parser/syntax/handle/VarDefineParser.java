package org.mmfmilku.atom.agent.compiler.parser.syntax.handle;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.Parser;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;

public class VarDefineParser implements ParserHandle {

    @Override
    public VarDefineStatement parse(Parser.ParserIterator iterator) {
        String varType = iterator.parseWordsPoint();
        Token varName = iterator.needNext(TokenType.Words);
        return new VarDefineStatement(varType, varName.getValue());
    }

}