package org.mmfmilku.atom.agent.compiler.parser.aa;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TestFile3 implements Serializable {
public TestFile3() {}


public void forCase1() {int b = 0;
for ( int i = 0 ; i < 10 ; i++ ) {System.out.println("for");
}
for ( int i = 0 ; i < 10 ; b = 3 ) {i++;
System.out.println("for");
}
}


public void forCase2() {List list = Arrays.asList(1, 2, 4);
for ( Object integer : list ) {System.out.println("enhance for");
}
}


public void forCase3() {int i = 10;
while ( i > 0 ) {System.out.println(i--);
List list = Arrays.asList(4, 6, 8);
for ( Object integer : list ) {System.out.println(integer);
}
}
}


public void forCase4() {int i = 10;
while ( i > 0 ) {System.out.println(i--);
}
}

}