package atom;


import param.Param;
import util.AssertUtils;

import java.util.function.Function;

@FunctionalInterface
public interface Atom<T extends Param> extends Function<T, Boolean> {

    Boolean execute(T param);

    @Override
    default Boolean apply(T t) {
        return execute(t);
    }

    default Atom<T> before(Atom<? super T> before) {
        AssertUtils.notNull(before);
        return t -> before.execute(t) && execute(t);
    }

    default Atom<T> after(Atom<? super T> after) {
        AssertUtils.notNull(after);
        return t -> execute(t) && after.execute(t);
    }
}
