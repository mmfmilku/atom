package org.mmfmilku.atom.agent.compiler.parser.handle.code.keyword;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.ParserIterator;
import org.mmfmilku.atom.agent.compiler.parser.handle.CodeParserHandle;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

/**
 * 解析 主动抛异常语句
 * */
public class ThrowParser implements CodeParserHandle {
    @Override
    public boolean match(ParserIterator iterator) {
        return false;
    }

    @Override
    public Statement parse(ParserIterator iterator) {
        GrammarUtil.notSupport();
        // TODO
        return null;
    }

}
