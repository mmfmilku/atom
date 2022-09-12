package org.mmfmilku.atom.test.atom;

import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.test.param.TestParam;

import java.util.Collections;

public class PrintParamAtom implements Atom<TestParam> {
    @Override
    public Boolean execute(TestParam param) {
        System.out.println("param:" + param.getAllParams());
        param.appendFlag("|print");
        return true;
    }
}
