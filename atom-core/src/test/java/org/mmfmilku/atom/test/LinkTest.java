package org.mmfmilku.atom.test;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.dispatcher.AtomChain;
import org.mmfmilku.atom.dispatcher.IntegrateAtomChain;
import org.mmfmilku.atom.dispatcher.LinkedAtomChain;
import org.mmfmilku.atom.dispatcher.DefaultAtomChain;
import org.junit.Test;
import org.mmfmilku.atom.param.Param;
import org.mmfmilku.atom.util.AtomUtils;

public class LinkTest {

    private Atom<MyParam> printA = printAtom("a");
    private Atom<MyParam> printB = printAtom("b");
    private Atom<MyParam> throwNPE = AtomUtils.toAtom(param -> {
        System.out.println("before throw");
        String a = null;
        a.length();
        System.out.println("after throw");
    });

    private MyAtom myHandle = param -> {
        System.out.println(param.value());
        return true;
    };

    private Atom<MyParam> printAtom(String printStr) {
        return AtomUtils.toAtom(() -> System.out.println(printStr));
    }

    @Test
    public void test1() {
        AtomChain<MyParam> linkedAtomChain = new LinkedAtomChain<>();
        linkedAtomChain
                .add(printA)
                .add(printB)
                .add(printAtom("c"))
                .add(myHandle)
                .add(AtomUtils.toAtom(param -> {
                    System.out.println(param.value());
                }));

        MyParam myParam = () -> "MyParam d";
        linkedAtomChain.invoke(myParam);
    }

    @Test
    public void test3() {
        AtomChain<MyParam> integrateAtomChain = new IntegrateAtomChain<>();
        integrateAtomChain
                .add(printA)
                .add(printB)
                .add(printAtom("c"))
                .add(myHandle)
                .add(AtomUtils.toAtom(param -> {
                    System.out.println(param.value());
                }));

        MyParam myParam = () -> "MyParam d";
        integrateAtomChain.invoke(myParam);
    }

    @Test
    public void test2() {
        MyParam myParamA = () -> "value";
        int i = 4;
        int j = 10;
        DefaultAtomChain<MyParam> defaultDispatcher = new DefaultAtomChain<>();
        defaultDispatcher
                .add(printA)
                .tryProcess(throwNPE)
                .catchProcess(printAtom("catch with throw"))
                .finallyProcess(printAtom("finally when throw"))
                .tryProcess(printAtom("not throw"))
                .catchProcess((e, param) -> {
                    System.out.println("catch without throw");
                    return false;
                })
                .finallyProcess(printAtom("finally when not throw"))
                .ifTrue(myParam -> true)
                .thenExecute(printAtom("condition1 true"))
                .then()
                .ifTrue(myParam -> false)
                .thenExecute(printAtom("condition2 true"))
                .elseExecute(printAtom("condition2 false"))
                .ifTrue(myParam -> i == 1)
                .thenExecute(printAtom("i == 1"))
                .ifTrue(myParam -> i == 2)
                .thenExecute(printAtom("i == 2"))
                .ifTrue(myParam -> i == 3)
                .thenExecute(printAtom("i == 3"))
                .ifTrue(myParam -> i == 4)
                .thenExecute(printAtom("i == 4"))
                .ifTrue(myParam -> i == 5)
                .thenExecute(printAtom("i == 5"))
                .elseExecute(printAtom("i not in [1,5]"))
                .ifTrue(myParam -> j == 7)
                .thenExecute(printAtom("j == 7"))
                .ifTrue(myParam -> j == 8)
                .thenExecute(printAtom("j == 8"))
                .ifTrue(myParam -> j == 9)
                .thenExecute(printAtom("j == 9"))
                .elseExecute(printAtom("j not in [7,9]"))
                .invoke(myParamA);
    }

    interface MyAtom extends Atom<MyParam> {
    }

    private interface MyParam extends Param {
        String value();
    }

}
