package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.MethodCall;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ExpStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ProxyNodeTest {

    interface Agentable {

        void setTarget(Statement target);

    }

    static class NodeProxy implements InvocationHandler {

        Statement target;

        NodeProxy(Statement target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("进入代理" + method.getName());
            String declaringClassName = method.getDeclaringClass().getName();
            if (declaringClassName.equals(Agentable.class.getName())) {
                this.target = (Statement) args[0];
                return null;
            }
            return method.invoke(target, args);
        }
    }

    @Test
    public void test() {

        VarDefineStatement s1 = new VarDefineStatement(
                "String", "str");
        System.out.println(s1.getSourceCode());
        System.out.println(s1.getStatementSource());
        InvocationHandler clientProxy = new NodeProxy(s1);
        Object o = Proxy.newProxyInstance(clientProxy.getClass().getClassLoader(),
                new java.lang.Class[]{Statement.class, Agentable.class}, clientProxy);
        Statement o1 = (Statement) o;
        System.out.println(o1.getSourceCode());
        System.out.println(o1.getStatementSource());
        System.out.println(o1.getAllExpression());
        System.out.println("---------切换target-------");
        if (o instanceof Agentable) {
            CodeBlock codeBlock = new CodeBlock();
            MethodCall methodCall = new MethodCall("goDo");
            List<Statement> statements = new ArrayList<>();
            statements.add(s1);
            statements.add(new ExpStatement(methodCall));
            codeBlock.setStatements(statements);
            codeBlock.getSourceCode();
            Agentable o2 = (Agentable) o;
            o2.setTarget(codeBlock);
        }
        System.out.println(o1.getAllExpression());
        System.out.println(o1.getSourceCode());
    }

}
