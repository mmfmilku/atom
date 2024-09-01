package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.List;

public class MethodCall implements Expression {

    String calledMethod;
    List<Expression> passedParams;

}
