package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

import java.util.HashMap;
import java.util.Map;

public class BaseLiteral implements Expression {

    private String value;

    public BaseLiteral(String value) {
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

    }
}
