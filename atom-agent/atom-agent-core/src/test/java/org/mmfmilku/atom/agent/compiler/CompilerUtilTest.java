package org.mmfmilku.atom.agent.compiler;

import org.junit.Test;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaAST;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.AccessPrivilege;
import org.mmfmilku.atom.agent.compiler.parser.syntax.deco.Modifier;
import org.mmfmilku.atom.agent.compiler.parser.syntax.express.Expression;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.CodeBlock;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.ExpStatement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.leaf.VarDefineStatement;
import org.mmfmilku.atom.agent.console.JScriptExecutor;
import org.mmfmilku.atom.agent.console.JTerminalExecutor;

import java.util.Collections;

public class CompilerUtilTest {


    @Test
    public void testNewEmptyJavaAST() {
        System.out.println(CompilerUtil.newEmptyJavaAST(JScriptExecutor.class).getSourceCode());

        System.out.println("------------------------");
        JavaAST javaAST = CompilerUtil.newEmptyJavaAST(JTerminalExecutor.class);
        System.out.println(javaAST.getSourceCode());

        // 构造echo方法
        Method echoMethod = new Method();

        Modifier modifier = new Modifier();
        modifier.setAccessPrivilege(AccessPrivilege.PRIVATE);
        echoMethod.setModifier(modifier);
        echoMethod.setMethodName("echo");
        VarDefineStatement varDefine = new VarDefineStatement("Object", "arg0");
        echoMethod.setMethodParams(Collections.singletonList(varDefine));
        echoMethod.setReturnType("void");
        echoMethod.setAnnotations(Collections.emptyList());
        CodeBlock codeBlock = new CodeBlock();
        Expression expression = CompilerUtil.parseExpression(
                "org.mmfmilku.atom.agent.log.ScreenLogger.print(org.mmfmilku.atom.agent.util.AgentExeUtils.toString(arg0))");
        codeBlock.setStatements(Collections.singletonList(new ExpStatement(expression)));
        echoMethod.setCodeBlock(codeBlock);

        javaAST.getClassList().get(0).getMethods().add(echoMethod);

        System.out.println(javaAST.getSourceCode());
    }

}