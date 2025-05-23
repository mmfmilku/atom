package org.mmfmilku.atom.agent.compiler.parser.handle.code;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.MethodReference;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;

public class MethodReferenceParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        // a::b
        return iterator.isCurr(TokenType.Words)
                && iterator.isNext(TokenType.Symbol, COLON)
                && iterator.isNext(2, TokenType.Symbol, COLON)
                && iterator.isNext(3, TokenType.Words);
    }

    @Override
    public MethodReference parse(ParserIterator iterator) {
        if (!match(iterator)) {
            iterator.throwIllegalToken(iterator.getCurr().getValue());
        }
        Identifier referenceObj = new Identifier(iterator.getCurr().getValue());
        iterator.needNext(TokenType.Symbol, COLON);
        iterator.needNext(TokenType.Symbol, COLON);
        Token token = iterator.needNext(TokenType.Words);
        String referenceName = token.getValue();
        return new MethodReference(referenceObj, referenceName);
    }
}
