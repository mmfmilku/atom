package org.mmfmilku.atom.agent.compiler.parser.aa;
import java.util.Arrays;

public class TestFile5 {static {System.out.println("static code");
}
static {System.out.println("static code 2");
}
private static final Integer lock = 0;

public static synchronized void test() {{System.out.println("code block");
synchronized (TestFile5.class) {System.out.println("synchronized in code block1");
synchronized (Arrays.class) {System.out.println("synchronized code block1-1");
}
}
}
synchronized (TestFile5.class) {System.out.println("synchronized code block2");
}
synchronized (lock) {System.out.println("synchronized code block3");
}
synchronized ((Integer) (lock + 1)) {System.out.println("synchronized code block4");
}
}

}