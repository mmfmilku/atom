package org.mmfmilku.atom.agent.compiler.parser.syntax;

public class Import implements Node {

    private String value;
    private boolean staticImp = false;

    public boolean isStaticImp() {
        return staticImp;
    }

    public void setStaticImp(boolean staticImp) {
        this.staticImp = staticImp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getSourceCode() {
        return (staticImp ? "import static " : "import ") + getValue() + ";";
    }
}
