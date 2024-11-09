package org.mmfmilku.atom.agent.compiler.parser.aa;
import org.mmfmilku.atom.util.StringUtils;
import java.io.Serializable;

public class TestFile2 implements Serializable {static String a;
public static final String b = "bb";
protected final transient String c = "ccc";
private volatile transient int d = 4444;
public static final transient int e = 5;

public TestFile2() {}


public TestFile2(String a, String b) {System.out.println((String) a + (String) b);
}


public synchronized void m1() {String a = (String) "344";
String b = StringUtils.right((String) a, (String) "3");
System.out.println(((Integer) Integer.valueOf(b)) + 4 + "f");
}

}