package org.mmfmilku.atom.agent.compiler.parser.syntax;

public enum Modifier implements Node {

    PUBLIC("public"),
    PROTECT("protect"),
    DEFAULT(""),
    PRIVATE("private");

    private String keyword;

    Modifier(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public static Modifier of(String keyword) {
        for (Modifier type : values()) {
            if (type.getKeyword().equals(keyword)) {
                return type;
            }
        }
        return null;
    }

}
