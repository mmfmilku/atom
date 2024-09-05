package org.mmfmilku.atom.agent.compiler.parser.syntax;

import java.util.List;

public class Method implements Node {

    private String methodName;
    private Modifier modifier;
    private List<MethodParam> methodParams;
    private String returnType;
    private CodeBlock codeBlock;
    private String value;

    static class MethodParam {
        String paramType;
        String paramName;
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

    public List<MethodParam> getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(List<MethodParam> methodParams) {
        this.methodParams = methodParams;
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
