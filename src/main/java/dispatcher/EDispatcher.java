package dispatcher;


import atom.Atom;

import java.util.function.BiConsumer;

public class EDispatcher extends DefaultDispatcher {

    @Override
    public BiConsumer<EDispatcher, Atom> operator(String operate) {
        BiConsumer operator = super.operator(operate);
        return super.operator(operate);
    }
}
