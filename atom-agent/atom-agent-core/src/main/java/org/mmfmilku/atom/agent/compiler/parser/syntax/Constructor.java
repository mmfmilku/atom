package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;

import java.util.stream.Collectors;

public class Constructor extends Method implements Node {

    public Constructor(String returnType) {
        this.returnType = returnType;
    }

    /**
     * 构造方法没有方法名
     * */
    @Override
    public String getMethodName() {
        GrammarUtil.notSupport();
        return null;
    }

    /**
     * 构造方法没有方法名
     * */
    @Override
    public void setMethodName(String methodName) {
        GrammarUtil.notSupport();
    }

    @Override
    public String getSourceCode() {
        return GrammarUtil.getLinesCode(annotations)
                + "\n"
                + GrammarUtil.getSentenceCode(modifier.getSourceCode(), returnType)
                + "("
                + methodParams.stream()
                .map(Statement::getStatementSource)
                .collect(Collectors.joining(", "))
                + ")"
                + (throwList == null || throwList.size() == 0 ? ""
                    : "throws " + String.join(", ", throwList)
                    )
                + " "
                + GrammarUtil.getLinesCode(codeBlock);
    }

}
