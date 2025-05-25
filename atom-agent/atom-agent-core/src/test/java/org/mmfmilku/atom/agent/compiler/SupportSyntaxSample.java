package org.mmfmilku.atom.agent.compiler;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支持的语法样例
 * */
public class SupportSyntaxSample<T>
        extends java.util.HashMap<String, T>
        implements Consumer<String>, java.util.function.Function<String, String> {
    public <G> SupportSyntaxSample(G g) {
        System.out.println(g);
    }

    public void syntaxSample() throws Exception {

        String var1;
        String var2 = "var2";
        var1 = "var1:" + var2;
        System.out.println(var1 + var2);

        int i1 = 0;
        while (i1 == 0) {
            i1 = (int) (System.currentTimeMillis() % 2);
        }
        if (i1 != 0) {
            System.out.println("i1 != 0");
        }
    }

    private static void defineStatement() {
        int i1 = 234;
        double d0 = 3423.342;

        long l1 = 123l;
        long l2 = 100_0000L;

        float f1 = 1.22f;
        float f2 = 0.34F;

        double d1 = 23432.23432d;
        double d2 = 0.000000043;
        double d3 = 999_000_111.342D;
    }

    public String[] arr(String[] strArr, int[] intArr) {
        int[] a;
        long[] b = new long[4];

        java.lang.String[] strArr1 = new String[3];
        java.lang.String[] strArr2 = new String[]{"a", "b", "c"};
        Map<String, java.lang.Object>[] mapArr1 = null;

        System.out.println(new Object[]{"atom"});

        Map[] mapArr2 = new java.util.HashMap[6];

        return new String[]{"a", "b", "c"};
    }

    public void testTypeCast() {
        Object o = 33;
        int o2i1 = (Integer) o + 44;
                int o2i2 = (java.lang.Integer) o + 44;

        String s1 = (String) "ff";
        int i1 = (Integer) 33;

        Object o2 = new String();
        String s = (String) o + "ff";
        Integer integer = (Integer) o + 333;
    }

    public StringBuilder getStr() {
        String a = new String("aaa");
        StringBuilder sb = new java.lang.StringBuilder();
        System.out.println(sb.toString());
        return new java.lang.StringBuilder();
    }

    public void syntaxTry() throws NoSuchMethodException {
        try {
            Class<?> clazz = Class.forName("org.mmfmilku.atom.agent.compiler.SupportSyntaxSample");
            Method clazzMethod = clazz.getMethod("syntaxTry");
        } catch (ClassNotFoundException e) {
            System.out.println("catch block 1");
            throw new RuntimeException("ddd");
        } catch (NoSuchMethodException e) {
            System.out.println("catch block 2");
            throw e;
        } finally {
            System.out.println("finally execute");
        }
    }

    protected void lambda() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "fsdf");
        map.put("b", "fffds");
        map.put("c", "23324444");
        map.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
        });
        map.forEach((k, v) -> System.out.println(k));
        Function<String, Integer> function = str -> str.length();
    }

    void methodReference() {
        List<String> stringList = Arrays.asList("a", "bbb", "fffff");
        Integer sum = stringList.stream().map(String::length).mapToInt(Integer::intValue).sum();
        System.out.println(sum);
    }

    void ternaryOperate() {
        boolean b1 = true;
        boolean b2 = b1 ? false : true;
        boolean b3 = b1 == true || b2 == false;
        int i = 0;
        int j = i == 0 ? 1 : 3;
        int k = (j == 0 ? i : j) + 3;
        int l = b1 ? 1 : 0;
        int m = b1 && b2 ? i == 1 ? 1 : 2 : l == 0 ? 3 : 4;
        int len = (k == 0 ? "ff" : "ggg").length();
    }

    static Map<String, List<Object>> MAP = new HashMap<String, List<java.lang.Object>>();

    protected <K, V extends Object> Map<K, V> generics(List<V> list,
                                                     BiFunction<String, Boolean, K> biFunction) {
        Map<String, Object> map1 = new HashMap<>();
        Map<String, List<Object>> map2 = new HashMap<String, List<java.lang.Object>>();

        Map<String, java.lang.Object>[] mapArr1 = null;
        synchronized (this) {
            String v1 = "a";
            v1 += list.toString();
            {
            }
            {
                System.out.println("list size=" + list.size());
            }
            List<String> strings = new ArrayList<>();
            BiFunction<String, Boolean, K> b2 = biFunction;
            return Collections.emptyMap();
        }
    }

    @Override
    public void accept(String s) {

    }

    @Override
    public String apply(String s) {
        return null;
    }
}
