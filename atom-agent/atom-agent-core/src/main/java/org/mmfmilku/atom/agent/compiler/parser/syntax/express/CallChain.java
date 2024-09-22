package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 调用链表达式 o1.m1().o2.m2()
 */
public class CallChain implements Expression {

    private Expression first;

    private Expression next;

    public CallChain(Expression first, Expression next) {
        this.first = first;
        this.next = next;
    }

    @Override
    public String getSourceCode() {
        return first.getSourceCode() + "." + next.getSourceCode();
    }

    public Expression getFirst() {
        return first;
    }

    public void setFirst(Expression first) {
        this.first = first;
    }

    public Expression getNext() {
        return next;
    }

    public void setNext(Expression next) {
        this.next = next;
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        first.useImports(importsMap);
        next.useImports(importsMap);
    }

    @Override
    public List<Expression> getBaseExpression() {
        return Stream.concat(first.getBaseExpression().stream(),
                next.getBaseExpression().stream())
                .collect(Collectors.toList());
    }
}
