package org.mmfmilku.atom.agent.compiler.parser.aa;
import org.mmfmilku.atom.util.StringUtils;

public class TestFile1 extends Object {
public TestFile1() {}


public TestFile1(String a, String b) {System.out.println((String) a + (String) b);
}


public void m1() {String a = (String) "344";
String b = StringUtils.right((String) a, (String) "3");
System.out.println(((Integer) Integer.valueOf(b)) + 4 + "f");
}

}