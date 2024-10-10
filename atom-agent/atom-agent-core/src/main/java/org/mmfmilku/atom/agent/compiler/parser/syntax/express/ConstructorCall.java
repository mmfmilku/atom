package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

public class ConstructorCall extends MethodCall {

    public ConstructorCall(String calledMethod) {
        super(calledMethod);
    }

    @Override
    public String getSourceCode() {
        return "new " + GrammarUtil.toCallSourceCode(getCalledMethod(), getPassedParams());
    }

}
