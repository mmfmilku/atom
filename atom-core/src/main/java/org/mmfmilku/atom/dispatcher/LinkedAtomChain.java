package org.mmfmilku.atom.dispatcher;


import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.exeption.AtomException;
import org.mmfmilku.atom.param.Param;
import org.mmfmilku.atom.util.AssertUtils;
import org.mmfmilku.atom.util.AtomConst;

import java.util.function.BiConsumer;

public class LinkedAtomChain<T extends Param> implements AtomChain<T>, AtomOperator<T> {

    private Node<T> head = new Node<>(param -> true);
    private Node<T> tail = head;
    private Atom<T> beforeAtom;
    private Atom<T> afterAtom;

    public LinkedAtomChain() {
    }

    public LinkedAtomChain(Atom<T> beforeAtom, Atom<T> afterAtom) {
        this.beforeAtom = beforeAtom;
        this.afterAtom = afterAtom;
    }

    public void beforeExecute(Atom<T> beforeAtom) {
        this.beforeAtom = beforeAtom;
    }

    public void afterExecute(Atom<T> afterAtom) {
        this.afterAtom = afterAtom;
    }

    @Override
    public LinkedAtomChain<T> add(Atom<T> atom) {
        Node<T> newNode = new Node<>(atom);
        tail.next = newNode;
        tail = newNode;
        return this;
    }

    @Override
    public Boolean invoke(T param) {
        Node<T> point = head.next;
        while (point != null) {
            System.out.println("execute...");
            beforeExecute(param);
            Atom<T> atom = point.atom;
            Boolean success = atom.execute(param);
            afterExecute(param);
            if (!Boolean.TRUE.equals(success))
                return success;
            point = point.next;
        }
        return true;
    }

    private void beforeExecute(T param) {
        if (beforeAtom != null)
            beforeAtom.execute(param);
    }

    private void afterExecute(T param) {
        if (afterAtom != null)
            afterAtom.execute(param);
    }

    @Override
    public void operate(String operate, Atom<T> atom) {
        if (!AtomOperatesConst.ADD.equals(operate)) {
            throw new AtomException(operate + " not support in DefaultAtomChain");
        }
        this.add(atom);
    }

    private static class Node<T extends Param> {
        Node(Atom<T> atom) {
            this.atom = atom;
        }

        Atom<T> atom;
        Node<T> next;
    }

    public <D extends LinkedAtomChain> BiConsumer<D, Atom> operator(String operate) {
        AssertUtils.notNull(operate);
        if ("ADD".equals(operate)) {
            return D::add;
        }
        return AtomConst.NO_OPERATE;
    }

}
