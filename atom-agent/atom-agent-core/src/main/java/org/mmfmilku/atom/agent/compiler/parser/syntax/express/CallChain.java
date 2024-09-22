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
        if (next instanceof MethodCall) {
            // TODO 内部类的情况
            first.useImports(importsMap);
            next.useImports(importsMap);
        } else if (next instanceof CallChain) {
            // TODO 内部类的情况
            if (!(first instanceof Identifier)) {
                first.useImports(importsMap);
            }
            CallChain callChain = (CallChain) next;
            importNoneIdentifier(callChain, importsMap);
        } else {
            first.useImports(importsMap);
        }
        // TODO fix 对于全类名的调用，会导致重复包名
        // com.xxx.Class -> com.xxx.com.xxx.Class
    }

    private static void importNoneIdentifier(CallChain callChain,
                                             Map<String, String> importsMap) {
        // import所有非标识符的表达式
        Expression first = callChain.first;
        Expression next = callChain.next;
        if (!(first instanceof Identifier)) {
            first.useImports(importsMap);
        }
        if (next instanceof CallChain) {
            importNoneIdentifier((CallChain) next, importsMap);
        } else if (!(first instanceof Identifier)) {
            next.useImports(importsMap);
        }
    }

    @Override
    public List<Expression> getBaseExpression() {
        return Stream.concat(first.getBaseExpression().stream(),
                next.getBaseExpression().stream())
                .collect(Collectors.toList());
    }
}
