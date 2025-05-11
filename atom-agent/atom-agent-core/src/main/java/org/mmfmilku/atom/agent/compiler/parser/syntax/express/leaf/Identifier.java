package org.mmfmilku.atom.agent.compiler.parser.syntax.express.leaf;

import org.mmfmilku.atom.agent.compiler.parser.syntax.extend.AbstractLinkedNode;

import java.util.Map;

public class Identifier extends AbstractLinkedNode implements LeafExpression {

    private String value;

    public Identifier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getSourceCode() {
        return getValue();
    }

    @Override
    public void useImports(Map<String, String> importsMap) {
        setValue(importsMap.getOrDefault(value, value));
    }
}
