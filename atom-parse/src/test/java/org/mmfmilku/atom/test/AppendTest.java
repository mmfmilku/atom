package org.mmfmilku.atom.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.dispatcher.DefaultAtomChain;
import org.mmfmilku.atom.parser.BaseAtomChainParser;
import org.mmfmilku.atom.parser.BaseDefinition;
import org.mmfmilku.atom.parser.ELParser;
import org.mmfmilku.atom.test.atom.AppendAtom;
import org.mmfmilku.atom.test.file.FileUtil;
import org.mmfmilku.atom.test.param.TestParam;
import org.mmfmilku.atom.util.AssertUtils;

import java.io.IOException;

public class AppendTest {

    @Test
    public void testAppend() throws IOException {
        Atom<TestParam> atom = new AppendAtom();
        String data = FileUtil.getData("F:\\dev\\project\\atom\\atom-parse\\src\\test\\test-resources\\Append.json");
        BaseAtomChainParser<TestParam> baseAtomChainParser = new BaseAtomChainParser<>(a -> atom, new ELParser<>(TestParam::put));
        DefaultAtomChain<TestParam> atomChain = baseAtomChainParser
                .parse(JSON.parseObject(data, BaseDefinition.class), DefaultAtomChain.class);
        TestParam param = new TestParam();
        atomChain.invoke(param);
        System.out.println("result:" + param.getFlags());
        assert "|111|996|007".equals(param.getFlags());
    }

}
