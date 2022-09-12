package org.mmfmilku.atom.test;

import org.junit.Test;
import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.dispatcher.DefaultAtomChain;
import org.mmfmilku.atom.test.atom.AppendAtom;
import org.mmfmilku.atom.test.param.TestParam;
import org.mmfmilku.atom.test.utils.FileUtil;
import org.mmfmilku.atom.test.utils.ParseUtil;
import org.mmfmilku.atom.util.AssertUtils;

import java.io.IOException;

public class NestAtomTest {

    @Test
    public void nestTest() throws IOException {
        String data = FileUtil.getData("F:\\dev\\project\\atom\\atom-parse\\src\\test\\test-resources\\CommonStart.json");
        DefaultAtomChain<TestParam> chainStart = ParseUtil.parse(data, DefaultAtomChain.class);
        String data2 = FileUtil.getData("F:\\dev\\project\\atom\\atom-parse\\src\\test\\test-resources\\CommonEnd.json");
        DefaultAtomChain<TestParam> chainEnd = ParseUtil.parse(data2, DefaultAtomChain.class);
        TestParam param = new TestParam();
        chainStart.add(chainEnd).invoke(param);
        System.out.println(param.getFlags());
        AssertUtils.assertTrue("|start|start|print|end|end|print".equals(param.getFlags()));
    }

}
