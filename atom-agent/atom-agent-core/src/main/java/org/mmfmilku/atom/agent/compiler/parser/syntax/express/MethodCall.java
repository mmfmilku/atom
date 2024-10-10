package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;

import java.util.*;
import java.util.stream.Collectors;

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
    public void useImports(Map<String, String> importsMap) {
        setCalledMethod(importsMap.getOrDefault(calledMethod, calledMethod));
        Optional.of(passedParams)
                .ifPresent(expressions -> expressions.forEach(
                        expression ->
                                expression.useImports(importsMap)));
    }

    @Override
    public List<Expression> getBaseExpression() {
        if (passedParams == null) {
            return Collections.emptyList();
        }
        return passedParams.stream()
                .map(Expression::getBaseExpression)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
