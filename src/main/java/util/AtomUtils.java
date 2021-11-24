package util;

import atom.Atom;
import atom.Process;
import param.Param;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AtomUtils {

    public static <T extends Param> Atom<T> toAtom(Consumer<T> consumer) {
        return param -> {
            consumer.accept(param);
            return true;
        };
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
