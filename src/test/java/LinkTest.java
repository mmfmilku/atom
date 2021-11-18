import atom.Atom;
import dispatcher.BaseDispatcher;
import dispatcher.DefaultDispatcher;
import org.junit.Test;
import param.Param;

public class LinkTest {

    private static Atom<MyParam> printA = param -> System.out.println("a");
    private static Atom<MyParam> printB = param -> System.out.println("b");
    private static Atom<MyParam> throwNPE = param -> {
        System.out.println("before throw");
        String a = null;
        a.length();
        System.out.println("after throw");
    };

    private static MyAtom myHandle = param -> System.out.println(param.value());

    @Test
    public void test1() {
        BaseDispatcher<MyParam> baseDispatcher = new BaseDispatcher<>();
        baseDispatcher
                .add(printA)
                .add(printB)
                .add(param -> System.out.println("c"))
                .add(myHandle)
                .add((MyAtom) param -> System.out.println(param.value()));

        MyParam myParam = () -> "MyParam d";
        baseDispatcher.invoke(myParam);
    }

    @Test
    public void test2() {
        MyParam myParamA = () -> "value";
        int i = 4;
        DefaultDispatcher<MyParam> defaultDispatcher = new DefaultDispatcher<>();
        defaultDispatcher
                .add(printA)
                .tryProcess(throwNPE)
                .catchProcess(param -> System.out.println("process with throw"))
                .finallyProcess(param -> System.out.println("finally when throw"))
                .tryProcess(param -> System.out.println("not throw"))
                .catchProcess((e, param) -> System.out.println("process without throw"))
                .finallyProcess(param -> System.out.println("finally when not throw"))
                .ifTrue(myParam -> true)
                .thenExecute(param -> System.out.println("condition1 true"))
                .ifTrue(myParam -> false)
                .thenExecute(param -> System.out.println("condition2 true"))
                .elseExecute(param -> System.out.println("condition2 false"))
                .ifTrue(myParam -> i == 1)
                .thenExecute(param -> System.out.println("i == 1"))
                .ifTrue(myParam -> i == 2)
                .thenExecute(param -> System.out.println("i == 2"))
                .ifTrue(myParam -> i == 3)
                .thenExecute(param -> System.out.println("i == 3"))
                .ifTrue(myParam -> i == 4)
                .thenExecute(param -> System.out.println("i == 4"))
                .ifTrue(myParam -> i == 5)
                .thenExecute(param -> System.out.println("i == 5"))
                .elseExecute(param -> System.out.println("i not in [1,5]"))
                .invoke(myParamA);
    }

    interface MyAtom extends Atom<MyParam> {
    }

    private interface MyParam extends Param {
        String value();
    }

}
