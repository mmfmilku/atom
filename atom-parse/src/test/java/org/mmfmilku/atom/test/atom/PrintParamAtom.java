package org.mmfmilku.atom.test.atom;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.test.param.TestParam;

import java.util.Collections;

public class PrintParamAtom implements Atom<TestParam> {
    @Override
    public Boolean execute(TestParam param) {
        param.appendFlag("|print");
        System.out.println("param:" + param.getAllParams());
        System.out.println("flags=" + param.getFlags());
        return true;
    }
}
