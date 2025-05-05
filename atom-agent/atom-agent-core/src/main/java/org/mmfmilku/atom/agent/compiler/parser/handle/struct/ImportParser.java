package org.mmfmilku.atom.agent.compiler.parser.handle.struct;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.HandleScope;
import org.mmfmilku.atom.agent.compiler.parser.handle.StructParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Import;

public class ImportParser implements StructParserHandle<Import> {
    @Override
    public boolean match(ParserIterator iterator) {
        return iterator.isCurr(TokenType.Words, "import");
    }

    @Override
    public Import parse(ParserIterator iterator) {
        iterator.checkCurr(TokenType.Words, "import");
        Import anImport = new Import();
        if (iterator.isNext(TokenType.Words, "static")) {
            // 处理 import static xxx
            anImport.setStaticImp(true);
            iterator.needNext();
        }
        Token token = iterator.needNext(TokenType.Words);
        StringBuilder value = new StringBuilder(token.getValue());
        while (!iterator.isNext(TokenType.Symbol, SEMICOLONS)) {
            iterator.needNext(TokenType.Symbol, POINT);
            Token next = iterator.needNext();
            value.append(POINT).append(next.getValue());
            if (next.getType() != TokenType.Words) {
                iterator.checkCurr(TokenType.Symbol, "*");
                // import xx.xx.* 最后一个为*
                break;
            }
        }
        iterator.needNext(TokenType.Symbol, SEMICOLONS);
        anImport.setValue(value.toString());
        return anImport;
    }

    @Override
    public int parseScope() {
        return HandleScope.OUT_CLASS.getDealMask();
    }
}
