package org.mmfmilku.atom.agent.compiler.parser.syntax.extend;

public abstract class AbstractLinkedNode implements LinkedNode {

    private LinkedNode parent;

    @Override
    public LinkedNode getParent() {
        return parent;
    }

    @Override
    public void setParent(LinkedNode parent) {
        this.parent = parent;
    }
}
