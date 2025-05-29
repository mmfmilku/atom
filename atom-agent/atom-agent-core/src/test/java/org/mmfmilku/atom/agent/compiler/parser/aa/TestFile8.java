package org.mmfmilku.atom.agent.compiler.parser.aa;
import java.util.*;

public class TestFile8 {
private void varAssignExp() {String a;
String b;
a = b = "fff";
int i1 = 3;
int i2 = i1 = a.length();
int i3;
int i4 = i3 = i2 = i1 = i1 + b.length() + i2;
List<String> list;
for ( String str : Collections.singletonList("f") ) {System.out.println(str);
}
for ( String str : list = Arrays.asList("zzzz", "42342", "fds", "fsdfs", "54") ) {System.out.println(str);
}
for ( Map<String , Object> map : arr(null, null) ) {System.out.println(map);
}
}


public Map<String , Object>[] arr(String[] strArr, int[] intArr) {int[] a;
long[] b = new long[4];
java.lang.String[] strArr1 = new String[3];
java.lang.String[] strArr2 = new String[]{"a", "b", "c"};
Map<String , java . lang . Object>[] mapArr1 = null;
System.out.println(new Object[]{"atom"});
Map[] mapArr2 = new java.util.HashMap[6];
return new Map[]{new HashMap<String , Object>(3), new HashMap()};
}

}