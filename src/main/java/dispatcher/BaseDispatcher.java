package dispatcher;


import atom.Atom;
import param.Param;
import util.AssertUtils;
import util.LambdaUtil;

import java.util.function.BiConsumer;

public class BaseDispatcher<T extends Param> implements Dispatcher<T> {

    private Node<T> head = new Node<>(param -> {
    });
    private Node<T> tail = head;
    private Atom<T> beforeAtom;
    private Atom<T> afterAtom;

    public BaseDispatcher() {
    }

    public BaseDispatcher(Atom<T> beforeAtom, Atom<T> afterAtom) {
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
    public BaseDispatcher<T> add(Atom<T> atom) {
        Node<T> newNode = new Node<>(atom);
        tail.next = newNode;
        tail = newNode;
        return this;
    }

    @Override
    public void invoke(T param) {
        Node<T> point = head.next;
        while (point != null) {
            System.out.println("execute...");
            beforeExecute(param);
            Atom<T> atom = point.atom;
            atom.execute(param);
            afterExecute(param);
            point = point.next;
        }
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
    public void execute(T param) {
        invoke(param);
    }

    private static class Node<T extends Param> {
        Node(Atom<T> atom) {
            this.atom = atom;
        }

        Atom<T> atom;
        Node<T> next;
    }

    protected BiConsumer NO_OPERATE = LambdaUtil.biNop();

    public <D extends BaseDispatcher> BiConsumer<D, Atom> operator(String operate) {
        AssertUtils.notNull(operate);
        switch (operate) {
            case "ADD":
                return D::add;
            default:
                return NO_OPERATE;
        }
    }

}
