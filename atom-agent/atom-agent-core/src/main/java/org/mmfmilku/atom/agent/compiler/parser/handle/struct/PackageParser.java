package org.mmfmilku.atom.agent.compiler.parser.handle.struct;

import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.HandleScope;
import org.mmfmilku.atom.agent.compiler.parser.handle.ParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Package;

public class PackageParser implements ParserHandle {

    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isCurr(TokenType.Words, "package");
    }

    @Override
    public Package parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "package");
        iterator.needNext(TokenType.Words);
        Package aPackage = new Package();
        String value = iterator.parseWordsPoint();
        iterator.needNext(TokenType.Symbol, SEMICOLONS);
        aPackage.setValue(value);
        return aPackage;
    }

    @Override
    public int parseScope() {
        return HandleScope.OUT_CLASS.getDealMask();
    }
}
