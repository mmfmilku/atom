package org.mmfmilku.atom.agent.compiler.parser.syntax;

public class Modifier implements Node {

    private ModifierType modifierType;

    enum ModifierType {

        PUBLIC("public"),
        PROTECT("protect"),
        DEFAULT("default_access"),
        PRIVATE("private");

        private String keyword;

        ModifierType(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }
    }

}
