package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.Identifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf.LeafExpression;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 调用链表达式 o1.m1().o2.m2()
 */
public class CallChain implements NestedExpression {

    private Expression[] children = new Expression[2];

    public CallChain(Expression first, Expression next) {
        children[0] = first;
        children[1] = next;
    }

    @Override
    public String getSourceCode() {
        return getFirst().getSourceCode() + "." + getNext().getSourceCode();
    }

    public Expression getFirst() {
        return children[0];
    }

    public Expression getNext() {
        return children[1];
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        Expression first = getFirst();
        Expression next = getNext();
        if (next instanceof MethodCall) {
            // Arrays.toString()    var.method()    get().len()
            // TODO 内部类的情况
            first.useImports(importsMap);
            next.useImports(importsMap);
        } else if (next instanceof CallChain) {
            // Collections.emptyList.size()
            // TODO 内部类的情况
            // TODO 变量名与类名相同的情况
//            if (!(first instanceof Identifier)) {
            first.useImports(importsMap);
//            }
            CallChain callChain = (CallChain) next;
            // 链式调用，标识符中只有第一位需要导入
            importAfterChain(callChain, importsMap);
        } else {
            first.useImports(importsMap);
        }
        // TODO fix 对于全类名的调用，会导致重复包名
        // com.xxx.Class -> com.xxx.com.xxx.Class
    }

    private static void importAfterChain(CallChain callChain,
                                         Map<String, String> importsMap) {
        // import所有非标识符的表达式
        Expression first = callChain.getFirst();
        Expression next = callChain.getNext();
        if (!(first instanceof Identifier)) {
            first.useImports(importsMap);
        }
        if (next instanceof CallChain) {
            importAfterChain((CallChain) next, importsMap);
        } else if (!(next instanceof Identifier)) {
            // 不只是标识符不做import替换
            if (next instanceof MethodCall) {
                // 方法调用，调用的方法本身不替换
                MethodCall methodCall = (MethodCall) next;
                Optional.of(methodCall.getPassedParams())
                        .ifPresent(expressions -> expressions.forEach(
                                expression -> expression.useImports(importsMap)));
            } else {
                next.useImports(importsMap);
            }
        }
    }

    @Override
    public List<LeafExpression> getLeafExpression() {
        Expression first = getFirst();
        Expression next = getNext();
        return Stream.concat(first.getLeafExpression().stream(),
                next.getLeafExpression().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Expression[] orinChildren() {
        return children;
    }

    @Override
    public List<Expression> getNested() {
        return Arrays.asList(children);
    }
}
