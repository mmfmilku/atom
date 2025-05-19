package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.handle.HandleScope;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Generics;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;

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
        iterator.checkCurr(TokenType.Words);
        String varType = iterator.parseWordsPoint();
        iterator.needNext();
        Generics generics = iterator.parseGenericsAndNext();
        Token varName = iterator.checkCurr(TokenType.Words);
        VarDefineStatement varDefineStatement =
                new VarDefineStatement(varType, varName.getValue());
        varDefineStatement.setGenerics(generics);
        return varDefineStatement;
    }

    @Override
    public int parseScope() {
        return HandleScope.assembly(HandleScope.IN_CODE_BLOCK, HandleScope.IN_CLASS);
    }

}
