package org.mmfmilku.atom.agent.compiler.parser.syntax.express;

public class StringLiteral extends BaseLiteral {

    public StringLiteral(String value) {
        super(value);
    }

    @Override
    public String getSourceCode() {
        return "\"" + super.getSourceCode()  + "\"";
    }
}
