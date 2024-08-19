package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

public class Operator implements Node {

    String value;

    public Operator(String value) {
        this.value = value;
    }
}
