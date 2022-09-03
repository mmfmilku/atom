package org.mmfmilku.atom.test;

import org.mmfmilku.atom.Atom;
import com.alibaba.fastjson.JSON;
import org.mmfmilku.atom.dispatcher.DefaultAtomChain;
import org.mmfmilku.atom.dispatcher.IntegrateAtomChain;
import org.mmfmilku.atom.dispatcher.LinkedAtomChain;
import org.junit.Test;
import org.mmfmilku.atom.param.BaseParam;
import org.mmfmilku.atom.parser.BaseDefinition;
import org.mmfmilku.atom.parser.BaseAtomChainParser;
import org.mmfmilku.atom.parser.ELParser;
import org.mmfmilku.atom.util.AssertUtils;

import java.util.HashMap;
import java.util.Map;

public class ParseTest {

    private static class MyParam extends BaseParam {
        MyParam(String value) {
            this.value = value;
        }

        private Map<String, String> map = new HashMap<>();
        private String value;

        public String get(String key) {
            return map.get(key);
        }

        String set(String key, String value) {
            return map.put(key, value);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private static Map<String, Atom<MyParam>> map = new HashMap<>();

    static {
        map.put("a", param -> {
            param.value += " [from after handle a] ";
            System.out.println("in handle a,value=" + param);
            return true;
        });
        map.put("b", param -> {
            param.value += " [from after handle b] ";
            System.out.println("in handle b,value=" + param);
            return true;
        });
        map.put("c", param -> {
            param.value += " [from after handle c] ";
            System.out.println("in handle c,value=" + param);
            return true;
        });
        map.put("d", param -> {
            param.value += " [from after handle d] ";
            System.out.println("in handle d,value=" + param);
            return true;
        });
        map.put("e", param -> {
            param.value += " [from after handle e] ";
            System.out.println("in handle e,value=" + param);
            return true;
        });
        map.put("print", param -> {
            System.out.println("in handle print,value=" + param.value + ",map=" + param.map);
            return true;
        });
        map.put("alwaysTrue", param -> true);
        map.put("alwaysFalse", param -> false);
        map.put("exception", param -> {
            System.out.println("here throw exception");
            AssertUtils.notNull(null);
            return true;
        });
        map.put("catch", param -> {
            System.out.println("in handle catch,value=" + param);
            if (param.getLastCause() != null) {
                System.out.println(param.getLastCause().getMessage());
            }
            return true;
        });
    }

    @Test
    public void test1() {
        String def1 = "{" +
                "\"name\":\"a1\"," +
                "\"statements\":[" +
                "{\"operate\":\"ADD\",\"atom\":\"a\"}" +
                "{\"operate\":\"ADD\",\"atom\":\"b\"}" +
                "]}";
        String def2 = "{" +
                "\"name\":\"a2\"," +
                "\"statements\":[" +
                "{\"operate\":\"ADD\",\"atom\":\"c\"}" +
                "{\"operate\":\"ADD\",\"atom\":\"d\"}" +
                "]}";
        BaseDefinition definition = JSON.parseObject(def1, BaseDefinition.class);
        BaseDefinition definition2 = JSON.parseObject(def2, BaseDefinition.class);
        IntegrateAtomChain<MyParam> parse = parse(definition);
        parse.invoke(new MyParam("p1"));
        IntegrateAtomChain<MyParam> parse1 = parse(definition2);
        parse1.invoke(new MyParam("p2"));
    }

    @Test
    public void test2() {
        String def = "{" +
                "\"name\":\"a2\"," +
                "\"statements\":[" +
                "{\"operate\":\"ADD\",\"atom\":\"print\"," +
                "\"postEL\":[" +
                "\"$SET(\\\"k1\\\",\\\"v1\\\")\"," +
                "\"$SET(\\\"k2\\\",\\\"v2\\\")\"" +
                "]}," +
                "{\"operate\":\"ADD\",\"atom\":\"print\"}" +
                "]}";
        BaseDefinition definition = JSON.parseObject(def, BaseDefinition.class);
        LinkedAtomChain<MyParam> parse = parse2(definition);
        parse.invoke(new MyParam("p1"));
    }

    @Test
    public void test3() {
        String def = "{" +
                "\"name\":\"a3\"," +
                "\"statements\":[" +
                "{\"operate\":\"ADD\",\"atom\":\"print\"," +
                "\"postEL\":[" +
                "\"$SET(\\\"k1\\\",\\\"111\\\")\"," +
                "\"$COPY(\\\"k1\\\",\\\"k2\\\")\"," +
                "\"$SET(\\\"k3\\\",\\\"777\\\")\"" +
                "]}," +
                "{\"operate\":\"ADD\",\"atom\":\"print\"}" +
                "]}";
        BaseDefinition definition = JSON.parseObject(def, BaseDefinition.class);
        DefaultAtomChain<MyParam> parse = parse3(definition);
        parse.invoke(new MyParam("pp"));
    }

    @Test
    public void test4() {
        String def = "{" +
                "\"name\":\"a4\"," +
                "\"statements\":[" +
                "{\"operate\":\"TRY\",\"atom\":\"exception\"}," +
                "{\"operate\":\"CATCH\",\"atom\":\"a\"}," +
                "{\"operate\":\"FINALLY\",\"atom\":\"b\"}," +
                "{\"operate\":\"TRY\",\"atom\":\"c\"}," +
                "{\"operate\":\"CATCH\",\"atom\":\"d\"}," +
                "{\"operate\":\"FINALLY\",\"atom\":\"e\"}" +
                "]}";
        BaseDefinition definition = JSON.parseObject(def, BaseDefinition.class);
        DefaultAtomChain<MyParam> parse = parse3(definition);
        parse.invoke(new MyParam("pp"));
    }

    private static IntegrateAtomChain<MyParam> parse(BaseDefinition definition) {
        BaseAtomChainParser<MyParam> baseParser = new BaseAtomChainParser<>(s -> map.get(s));
        IntegrateAtomChain<MyParam> parse = baseParser.parse(definition, IntegrateAtomChain.class);
        return baseParser.parse(definition, IntegrateAtomChain.class);
    }

    private static LinkedAtomChain<MyParam> parse2(BaseDefinition definition) {
        BaseAtomChainParser<MyParam> baseParser = new BaseAtomChainParser<>(s -> map.get(s),
                new ELParser<>(MyParam::set));
        return baseParser.parse(definition, LinkedAtomChain.class);
    }

    private static DefaultAtomChain<MyParam> parse3(BaseDefinition definition) {
        BaseAtomChainParser<MyParam> baseParser = new BaseAtomChainParser<>(s -> map.get(s),
                new ELParser<>(MyParam::set
                        , MyParam::get
                        , (param, source, target) -> param.set(target, param.get(source))));
        return baseParser.parse(definition, DefaultAtomChain.class);
    }

}
