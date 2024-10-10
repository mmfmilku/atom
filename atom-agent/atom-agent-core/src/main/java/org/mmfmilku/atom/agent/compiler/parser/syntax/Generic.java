package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

public class Generic implements Node {

    @Override
    public String getSourceCode() {
        GrammarUtil.notSupport();
        return null;
    }

}
