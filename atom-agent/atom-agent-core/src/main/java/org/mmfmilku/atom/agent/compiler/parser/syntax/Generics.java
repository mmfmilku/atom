package org.mmfmilku.atom.agent.compiler.parser.syntax;

import org.mmfmilku.atom.agent.compiler.GrammarUtil;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;

/**
 * 泛形
 * */
public class Generics implements Node {

    /**
     * 泛形内容，暂时这么写
     * */
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getSourceCode() {
//        return TokenType.LAngle.getFixValue() + content + TokenType.RAngle.getFixValue();
        return "";
    }

}
