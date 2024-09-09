package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ConstructorCall implements Expression {

    private String calledMethod;
    private List<Expression> passedParams;

    public ConstructorCall(String calledMethod) {
        this.calledMethod = calledMethod;
    }

    public String getCalledMethod() {
        return calledMethod;
    }

    public void setCalledMethod(String calledMethod) {
        this.calledMethod = calledMethod;
    }

    public List<Expression> getPassedParams() {
        return passedParams;
    }

    public void setPassedParams(List<Expression> passedParams) {
        this.passedParams = passedParams;
    }

    @Override
    public String getSourceCode() {
        return "new " + GrammarUtil.toCallSourceCode(calledMethod, passedParams);
    }
}
