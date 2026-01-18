package org.mmfmilku.atom.test;

import org.junit.Test;
import org.mmfmilku.atom.Atom;
import org.mmfmilku.atom.dispatcher.DefaultAtomChain;
import org.mmfmilku.atom.param.DefaultParam;

/**
 * 使用示例
 * */
public class UseExampleTest {

    /**
     * 1.自定义参数类
     * */
    private static class ExampleParam extends DefaultParam {
        private String paramNo;

        public String getParamNo() {
            return paramNo;
        }

        public void setParamNo(String paramNo) {
            this.paramNo = paramNo;
        }
    }

    /**
     * 2.定义原子业务
     * */
    private static class StartBiz implements Atom<ExampleParam> {
        @Override
        public Boolean execute(ExampleParam param) {
            System.out.println("start biz!");
            return true;
        }
    }
    private static class CheckParamNo implements Atom<ExampleParam> {
        @Override
        public Boolean execute(ExampleParam param) {
            System.out.println("check biz!");
            return param != null && param.getParamNo() != null;
        }
    }
    private static class SomeBiz1 implements Atom<ExampleParam> {
        @Override
        public Boolean execute(ExampleParam param) {
            System.out.println("some biz 1!");
            return true;
        }
    }
    private static class SomeBiz2 implements Atom<ExampleParam> {
        @Override
        public Boolean execute(ExampleParam param) {
            System.out.println("some biz 2!");
            return true;
        }
    }
    private static class ErrorBiz implements Atom<ExampleParam> {
        @Override
        public Boolean execute(ExampleParam param) {
            System.out.println("error biz!");
            return true;
        }
    }
    private static class EndBiz implements Atom<ExampleParam> {
        @Override
        public Boolean execute(ExampleParam param) {
            System.out.println("end biz!");
            return true;
        }
    }

    /**
     * 具体接口实现
     * */
    private static class ServiceA {

        public void service(ExampleParam param) {
            DefaultAtomChain<ExampleParam> defaultAtomChain = new DefaultAtomChain<>();
            /***
             *  if (CheckBiz) {
             *      StartBiz
             *      SomeBiz1
             *  } else {
             *      ErrorBiz
             *  }
             *  EndBiz
             *
             */
            defaultAtomChain
                    .ifTrue(new CheckParamNo())
                    .thenExecute(new StartBiz().after(new SomeBiz1()))
                    .elseExecute(new ErrorBiz())
                    .add(new EndBiz())
            ;
            defaultAtomChain.execute(param);
        }

    }

    @Test
    public void test() {
        ExampleParam p1 = new ExampleParam();
        ExampleParam p2 = new ExampleParam();
        p2.setParamNo("param-no-2");

        ServiceA serviceA = new ServiceA();
        System.out.println("-----------------call service------------------");
        serviceA.service(p1);
        System.out.println("-----------------call service------------------");
        serviceA.service(p2);

    }

    /**
     * 低码方式
     * */
    @Test
    public void testLowCode() {

    }

}
