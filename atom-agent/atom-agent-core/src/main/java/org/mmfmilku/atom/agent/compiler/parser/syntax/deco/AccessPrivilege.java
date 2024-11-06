package org.mmfmilku.atom.agent.compiler.parser.syntax.deco;

import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

public enum AccessPrivilege implements Node {

    PUBLIC("public", 0b1000),
    PROTECT("protect", 0b0100),
    DEFAULT("", 0b0010),
    PRIVATE("private", 0b0001);

    private String keyword;

    private int level;

    AccessPrivilege(String keyword, int level) {
        this.keyword = keyword;
        this.level = level;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getLevel() {
        return level;
    }

    public static AccessPrivilege of(String keyword) {
        for (AccessPrivilege type : values()) {
            if (type.getKeyword().equals(keyword)) {
                return type;
            }
        }
        return null;
    }

    public static AccessPrivilege of(Token token) {
        return of(token.getValue());
    }

    @Override
    public String getSourceCode() {
        return getKeyword();
    }
}
