package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

/**
 * 调用链表达式 o1.m1().o2.m2()
 * */
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
}
