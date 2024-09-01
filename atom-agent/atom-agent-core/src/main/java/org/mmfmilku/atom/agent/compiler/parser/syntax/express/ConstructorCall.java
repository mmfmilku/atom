package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.List;

public class ConstructorCall implements Expression {

    String calledMethod;
    List<Expression> passedParams;

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
}
