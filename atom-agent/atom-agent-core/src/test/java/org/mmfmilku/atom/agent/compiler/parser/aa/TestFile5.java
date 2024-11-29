package org.mmfmilku.atom.agent.compiler.parser.aa;

public class TestFile5 {

    static {
        System.out.println("static code");
    }

    public static synchronized void test() {

        {
            System.out.println("code block");

            synchronized (TestFile5.class) {
                System.out.println("synchronized in code block");
            }
        }

        synchronized (TestFile5.class) {
            System.out.println("synchronized code block");
        }

    }

    static {
        System.out.println("static code 2");
    }

}
