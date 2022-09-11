package org.mmfmilku.atom.test.utils;

import com.alibaba.fastjson.JSON;
import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.dispatcher.AtomChain;
import org.mmfmilku.atom.dispatcher.DefaultAtomChain;
import org.mmfmilku.atom.parser.BaseAtomChainParser;
import org.mmfmilku.atom.parser.BaseDefinition;
import org.mmfmilku.atom.parser.ELParser;
import org.mmfmilku.atom.test.param.TestParam;

import java.util.function.Function;

public class ParseUtil {

    public static  <R extends AtomChain<TestParam>> R parse(String data, Class<R> clazz) {
        BaseAtomChainParser<TestParam> baseAtomChainParser = new BaseAtomChainParser<TestParam>(className -> {
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
