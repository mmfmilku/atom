package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.Map;

public class Identifier implements Expression {

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
