package org.mmfmilku.atom.test.utils;

import com.alibaba.fastjson.JSON;
import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.dispatcher.AtomChain;
import org.mmfmilku.atom.dispatcher.DefaultAtomChain;
import org.mmfmilku.atom.parser.BaseAtomChainParser;
import org.mmfmilku.atom.parser.BaseDefinition;
import org.mmfmilku.atom.parser.ELParser;
import org.mmfmilku.atom.test.param.TestParam;

import java.io.IOException;
import java.util.function.Function;

public class ParseTestUtil {

    private static final String TEST_RESOURCE_BATH_PATH = "F:\\dev\\project\\atom\\atom-parse\\src\\test\\test-resources\\";

    public static DefaultAtomChain<TestParam> parse(String path) throws IOException {
        String data = FileUtil.getData(path);
        DefaultAtomChain<TestParam> atomChain = ParseTestUtil.parse(data, DefaultAtomChain.class);
        return atomChain;
    }

    public static  <R extends AtomChain<TestParam>> R parse(String data, Class<R> clazz) {
        BaseAtomChainParser<TestParam> baseAtomChainParser = new BaseAtomChainParser<TestParam>(className -> {
            if (className.startsWith("$")) {
                try {
                    return parse(TEST_RESOURCE_BATH_PATH + className.substring(1) + ".json");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Atom atom = null;
            try {
                Class<Atom> forName = (Class<Atom>) Class.forName("org.mmfmilku.atom.test.atom." + className);
                atom = forName.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return atom;
        }, new ELParser<>(TestParam::put));
        R atomChain = baseAtomChainParser
                .parse(JSON.parseObject(data, BaseDefinition.class), clazz);
        return atomChain;
    }

    public static  <R extends AtomChain<TestParam>> R parse(Function<String, Atom<TestParam>> handleFactory,
                                                            String data, Class<R> clazz) {
        BaseAtomChainParser<TestParam> baseAtomChainParser = new BaseAtomChainParser<>(handleFactory,
                new ELParser<>(TestParam::put));
        R atomChain = baseAtomChainParser
                .parse(JSON.parseObject(data, BaseDefinition.class), clazz);
        return atomChain;
    }

}
