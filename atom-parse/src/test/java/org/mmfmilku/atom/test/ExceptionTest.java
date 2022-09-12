package org.mmfmilku.atom.test;

import org.junit.Test;
import org.mmfmilku.atom.dispatcher.DefaultAtomChain;
import org.mmfmilku.atom.test.param.TestParam;
import org.mmfmilku.atom.test.utils.FileUtil;
import org.mmfmilku.atom.test.utils.ParseUtil;
import org.mmfmilku.atom.util.AssertUtils;

import java.io.IOException;
import java.util.Arrays;

public class ExceptionTest {

    @Test
    public void notThrowTest() throws IOException {
        String data = FileUtil.getData("F:\\dev\\project\\atom\\atom-parse\\src\\test\\test-resources\\Exception.json");
        DefaultAtomChain<TestParam> atomChain = ParseUtil.parse(data, DefaultAtomChain.class);
        TestParam param = new TestParam();
        param.put("list", Arrays.asList("9", "3", "7", "8"));
        atomChain.invoke(param);
        String sumList = param.get("sumList");
        System.out.println(sumList);
        System.out.println(param.getFlags());
        AssertUtils.assertTrue("|finally".equals(param.getFlags()));
        AssertUtils.assertTrue("27".equals(sumList));

        param.put("list", Arrays.asList("9", "9", "0", "7", "50"));
        atomChain.invoke(param);
        sumList = param.get("sumList");
        System.out.println(sumList);
        System.out.println(param.getFlags());
        AssertUtils.assertTrue("|finally|finally".equals(param.getFlags()));
        AssertUtils.assertTrue("75".equals(sumList));
    }

    @Test
    public void throwTest() throws IOException {
        String data = FileUtil.getData("F:\\dev\\project\\atom\\atom-parse\\src\\test\\test-resources\\Exception.json");
        DefaultAtomChain<TestParam> atomChain = ParseUtil.parse(data, DefaultAtomChain.class);
        TestParam param = new TestParam();
        // sum a would throw NumberFormatException
        param.put("list", Arrays.asList("9", "3", "7", "a"));
        atomChain.invoke(param);
        String sumList = param.get("sumList");
        System.out.println(sumList);
        System.out.println(param.getFlags());
        System.out.println("the exception message is " + param.getLastCause());
        AssertUtils.assertTrue("|catch|finally".equals(param.getFlags()));
        AssertUtils.assertTrue(sumList == null);

    }

}
