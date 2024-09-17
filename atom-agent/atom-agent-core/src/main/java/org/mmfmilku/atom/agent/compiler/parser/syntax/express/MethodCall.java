package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MethodCall implements Expression {

    private String calledMethod;
    private List<Expression> passedParams;

    public MethodCall(String calledMethod) {
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
        return GrammarUtil.toCallSourceCode(calledMethod, passedParams);
    }

    @Override
    public void useImports(HashMap<String, String> importsMap) {
        setCalledMethod(importsMap.getOrDefault(calledMethod, calledMethod));
        Optional.of(passedParams)
                .ifPresent(expressions -> expressions.forEach(
                        expression ->
                                expression.useImports(importsMap)));
    }
}
