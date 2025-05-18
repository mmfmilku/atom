package org.mmfmilku.atom.agent.compiler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * 支持的语法样例
 * */
public class SupportSyntaxSample<T> implements Consumer<String> {

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

    public StringBuilder getStr() {
        String a = new String("aaa");
        StringBuilder sb = new java.lang.StringBuilder();
        System.out.println(sb.toString());
        return new java.lang.StringBuilder();
    }

    public void syntaxTry() {
        try {
            Class<?> clazz = Class.forName("org.mmfmilku.atom.agent.compiler.SupportSyntaxSample");
            Method clazzMethod = clazz.getMethod("syntaxTry");
        } catch (ClassNotFoundException e) {
            System.out.println("catch block 1");
//            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.out.println("catch block 2");
//            e.printStackTrace();
        } finally {
            System.out.println("finally execute");
        }
    }

//    protected <K, V extends Object> Map<K, V> getMap(List<V> list,
//                                                     BiFunction<String, Boolean, K> biFunction) {
//        synchronized (this) {
//            String v1 = "a";
//            v1 += list.toString();
//            {
//            }
//            {
//                System.out.println("list size=" + list.size());
//            }
//            List<String> strings = new ArrayList<>();
//            BiFunction<String, Boolean, K> b2 = biFunction;
//            return Collections.emptyMap();
//        }
//    }

    @Override
    public void accept(String s) {

    }
}
