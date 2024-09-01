package org.mmfmilku.atom.agent.compiler.parser.syntax;

import java.util.List;

public class Method implements Node {

    // TODO
    String methodName;
    List<MethodParam> methodParams;
    String returnType;
    private String value;

    static class MethodParam {
        String paramType;
        String paramName;
    }
}
