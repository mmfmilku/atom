package org.mmfmilku.atom.test.atom;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.test.param.TestParam;

public class AppendAtom implements Atom<TestParam> {

    public static final String APPEND_KEY = "APPEND_KEY";

    @Override
    public Boolean execute(TestParam param) {
        String toAppend = "|" + param.get(APPEND_KEY, "");
        System.out.println("execute in AppendAtom, append " + toAppend);
        param.appendFlag(toAppend);
        return true;
    }
}
