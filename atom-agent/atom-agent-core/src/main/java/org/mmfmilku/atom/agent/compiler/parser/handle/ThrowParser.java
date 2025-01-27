package org.mmfmilku.atom.agent.compiler.parser.handle;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.ParserDispatcher;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

/**
 * 解析 主动抛异常语句
 * */
public class ThrowParser implements ParserHandle {
    @Override
    public Statement parse(ParserDispatcher.ParserIterator iterator) {
        GrammarUtil.notSupport();
        // TODO
        return null;
    }

    @Override
    public int parseScope() {
        return 0;
    }
}
