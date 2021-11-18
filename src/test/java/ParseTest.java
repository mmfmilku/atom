import atom.Atom;
import com.alibaba.fastjson.JSON;
import dispatcher.BaseDispatcher;
import org.junit.Test;
import param.Param;
import parser.BaseDefinition;
import parser.BaseParser;
import parser.ELParser;

import java.util.HashMap;
import java.util.Map;

public class ParseTest {

    private static class MyParam implements Param {
        public MyParam(String value) {
            this.value = value;
        }

        private Map<String,String> map = new HashMap<>();
        private String value;
        public String get(String key) {
            return map.get(key);
        }
        public String set(String key, String value) {
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
        });
        map.put("b", param -> {
            param.value += " [from after handle b] ";
            System.out.println("in handle b,value=" + param);
        });
        map.put("c", param -> {
            param.value += " [from after handle c] ";
            System.out.println("in handle c,value=" + param);
        });
        map.put("d", param -> {
            param.value += " [from after handle d] ";
            System.out.println("in handle d,value=" + param);
        });
        map.put("print", param -> {
            System.out.println("in handle print,value=" + param.value + ",map=" + param.map);
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
        BaseDispatcher<MyParam> parse = parse(definition);
        parse.invoke(new MyParam("p1"));
        BaseDispatcher<MyParam> parse1 = parse(definition2);
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
        BaseDispatcher<MyParam> parse = parse2(definition);
        parse.invoke(new MyParam("p1"));
    }

    public static BaseDispatcher<MyParam> parse(BaseDefinition definition) {
        BaseParser<MyParam> baseParser = new BaseParser<>(s -> map.get(s));
        return baseParser.parse(definition);
    }

    public static BaseDispatcher<MyParam> parse2(BaseDefinition definition) {
        BaseParser<MyParam> baseParser = new BaseParser<>(s -> map.get(s),
                new ELParser<>(MyParam::set));
        return baseParser.parse(definition);
    }

}
