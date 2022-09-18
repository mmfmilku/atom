package org.mmfmilku.atom.test.atom;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.test.param.TestParam;

import java.util.Collections;

public class StartAtom implements Atom<TestParam> {
    @Override
    public Boolean execute(TestParam param) {
        String printSymbol = param.get("printSymbol");
        if (printSymbol == null || printSymbol.length() == 0) {
            printSymbol = "-";
        }
        String join = String.join(printSymbol, Collections.nCopies(20, ""));
        System.out.println(join + "start" + join);
        param.appendFlag("|start");
        return true;
    }
}
