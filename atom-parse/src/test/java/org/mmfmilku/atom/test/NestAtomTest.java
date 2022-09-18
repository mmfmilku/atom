package org.mmfmilku.atom.test;

import org.junit.Test;
import org.mmfmilku.atom.dispatcher.DefaultAtomChain;
import org.mmfmilku.atom.test.param.TestParam;
import org.mmfmilku.atom.test.utils.FileUtil;
import org.mmfmilku.atom.test.utils.ParseTestUtil;
import org.mmfmilku.atom.util.AssertUtils;

import java.io.IOException;

public class NestAtomTest {

    @Test
    public void nestTest() throws IOException {
        String data = FileUtil.getData("F:\\dev\\project\\atom\\atom-parse\\src\\test\\test-resources\\CommonStart.json");
        DefaultAtomChain<TestParam> chainStart = ParseTestUtil.parse(data, DefaultAtomChain.class);
        String data2 = FileUtil.getData("F:\\dev\\project\\atom\\atom-parse\\src\\test\\test-resources\\CommonEnd.json");
        DefaultAtomChain<TestParam> chainEnd = ParseTestUtil.parse(data2, DefaultAtomChain.class);
        TestParam param = new TestParam();
        chainStart.add(chainEnd).invoke(param);
        AssertUtils.assertTrue("|start|start|print|end|end|print".equals(param.getFlags()));
    }

    @Test
    public void nestaTest2() throws IOException {
        DefaultAtomChain<TestParam> atomChain = ParseTestUtil.parse("F:\\dev\\project\\atom\\atom-parse\\src\\test\\test-resources\\NestChain.json");
        TestParam param = new TestParam();
        atomChain.invoke(param);
        AssertUtils.assertTrue("|start|start|print|add one|add two|end|end|print".equals(param.getFlags()));
    }



}
