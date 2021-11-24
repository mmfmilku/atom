package util;

import atom.Atom;
import param.Param;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LambdaUtil {

    public static <T> Consumer<T> nop() {
        return t -> {
        };
    }

    public static <T, U> BiConsumer<T, U> biNop() {
        return (t, u) -> {
        };
    }

    public static <T extends Param> Atom<T> nopAtom() {
        return param -> true;
    }

    public static <T extends Param> Atom<T> nonNull(Atom<T> atom) {
        return atom == null ? param -> true : atom;
    }

    public static <T> Function<T, T> same() {
        return t -> t;
    }

    public static <T, R> Function<T, R> fixed(R r) {
        return t -> r;
    }

    public static <T> Supplier<T> supplier(T t) {
        return () -> t;
    }
}
