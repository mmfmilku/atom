package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.Statement;
import org.mmfmilku.atom.agent.compiler.parser.syntax.statement.VarDefineStatement;

import java.util.List;
import java.util.stream.Collectors;

public class Method implements Node {

    private List<Annotation> annotations;
    private String methodName;
    private Modifier modifier;
    private List<VarDefineStatement> methodParams;
    private List<String> throwList;
    private String returnType;
    private CodeBlock codeBlock;
    private String value;

    @Override
    public String getSourceCode() {
        return GrammarUtil.getLinesCode(annotations)
                + "\n"
                + GrammarUtil.getSentenceCode(modifier.getSourceCode(), returnType, methodName)
                + "("
                + methodParams.stream()
                    .map(Statement::getStatementSource)
                    .collect(Collectors.joining(", "))
                + ")"
                + (throwList == null || throwList.size() == 0 ? ""
                    : "throws " + String.join(", ", throwList)
                    )
                + GrammarUtil.getLinesCode(codeBlock);
    }

    public List<String> getThrowList() {
        return throwList;
    }

    public void setThrowList(List<String> throwList) {
        this.throwList = throwList;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public List<VarDefineStatement> getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(List<VarDefineStatement> methodParams) {
        this.methodParams = methodParams;
    }

    public CodeBlock getCodeBlock() {
        return codeBlock;
    }

    public void setCodeBlock(CodeBlock codeBlock) {
        this.codeBlock = codeBlock;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
