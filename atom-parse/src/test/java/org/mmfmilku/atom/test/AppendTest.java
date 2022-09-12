package org.mmfmilku.atom.test;

import org.junit.Test;
import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.dispatcher.DefaultAtomChain;
import org.mmfmilku.atom.test.atom.AppendAtom;
import org.mmfmilku.atom.test.utils.FileUtil;
import org.mmfmilku.atom.test.param.TestParam;
import org.mmfmilku.atom.test.utils.ParseUtil;
import org.mmfmilku.atom.util.AssertUtils;

import java.io.IOException;

public class AppendTest {

    @Test
    public void testAppend() throws IOException {
        Atom<TestParam> atom = new AppendAtom();
        String data = FileUtil.getData("F:\\dev\\project\\atom\\atom-parse\\src\\test\\test-resources\\Append.json");
        DefaultAtomChain<TestParam> atomChain = ParseUtil.parse(a -> atom, data, DefaultAtomChain.class);
        TestParam param = new TestParam();
        atomChain.invoke(param);
        System.out.println("result:" + param.getFlags());
        AssertUtils.assertTrue("|111|996|007".equals(param.getFlags()));
    }

}
