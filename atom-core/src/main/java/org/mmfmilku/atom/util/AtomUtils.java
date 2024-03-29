package org.mmfmilku.atom.util;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.Process;
import org.mmfmilku.atom.param.Param;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AtomUtils {

    public static <T extends Param> Atom<T> nopAtom() {
        return param -> Boolean.TRUE;
    }

    public static <T extends Param> Atom<T> awaysSuccess() {
        return nopAtom();
    }

    public static <T extends Param> Atom<T> awaysFail() {
        return param -> Boolean.FALSE;
    }

    public static <T extends Param> Atom<T> toAtom(Consumer<T> consumer) {
        return param -> {
            consumer.accept(param);
            return true;
        };
    }

    public static <T extends Param> Atom<T> toAtom(Predicate<T> predicate) {
        return predicate::test;
    }

    public static <T extends Param> Atom<T> toAtom(Supplier<Boolean> supplier) {
        return param -> supplier.get();
    }

    public static <T extends Param> Atom<T> toAtom(Process process) {
        return param -> {
            process.process();
            return true;
        };
    }

}
