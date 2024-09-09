package org.mmfmilku.atom.agent.compiler.parser.syntax;

public class Import implements Node {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getSourceCode() {
        return "import" + getValue() + ";";
    }
}
