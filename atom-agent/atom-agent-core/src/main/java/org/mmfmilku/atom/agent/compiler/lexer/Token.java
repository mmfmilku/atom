package org.mmfmilku.atom.agent.compiler.lexer;

/**
 * Token
 *
 * @author chenxp
 * @date 2024/8/6:14:18
 */
public class Token {

    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (type == TokenType.BlockComment || type == TokenType.Comment) {
            return "";
        }
        return type.getShowPrefix() + value + type.getShowSuffix();
    }
}
