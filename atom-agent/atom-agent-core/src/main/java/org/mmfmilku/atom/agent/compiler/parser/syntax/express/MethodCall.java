package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.List;

public class MethodCall implements Expression {

    private String calledMethod;
    private List<Expression> passedParams;

    public MethodCall(String calledMethod, List<Expression> passedParams) {
        this.calledMethod = calledMethod;
        this.passedParams = passedParams;
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
}
