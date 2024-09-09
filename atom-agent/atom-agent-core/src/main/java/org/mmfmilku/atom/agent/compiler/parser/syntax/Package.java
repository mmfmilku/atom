package org.mmfmilku.atom.agent.compiler.parser.syntax;

public class Package implements Node {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getSourceCode() {
        return "package" + getValue() + ";";
    }
}
