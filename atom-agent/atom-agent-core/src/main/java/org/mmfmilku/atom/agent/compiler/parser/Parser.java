package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

import java.util.List;

/**
 * Parser
 *
 * @author chenxp
 * @date 2024/8/8:16:34
 */
public class Parser {
    
    private Lexer lexer;
    
    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    private class ParserHandle {
        List<Token> tokens;
        int curr = 0;
        int peekPoint = 0;
        Token dealToken;

        private ParserHandle() {
            tokens = lexer.getTokens();
        }

        private void execute() {
            parseProgram();
            while (curr < tokens.size()) {
                dealToken = tokens.get(curr);
                Node node = parseProgram();
            }
        }
        
        private Node parseProgram() {
            while (curr < tokens.size()) {
                dealToken = tokens.get(curr);
                if (dealToken.getType() ==  TokenType.Words) {
                    String value = dealToken.getValue();
                    if ("import".equals(value)) {
                        Node node = parseImport();
                        continue;
                    }
                    if ("public class".equals(value)) {
                        Node node = parseImport();
                        continue;
                    }
                }
            }
            return null;
        }

        private Node parseImport() {
            return null;
        }

        private Node parseClass() {
            if (true) {
                
            }
            return null;
        }

        private Node parseMethod() {
            if (true) {

            }
            return null;
        }

        private Node parseStatement() {
            if (true) {

            }
            return null;
        }

        private Node parseIf() {
            if (true) {

            }
            return null;
        }

        private Node parseWhile() {
            if (true) {

            }
            return null;
        }

        private Node parseFor() {
            if (true) {

            }
            return null;
        }

        /**
         *
         * */
        private Token peekNext() {
            int peekPoint = curr + 1;
            if (peekPoint < tokens.size()) {
                return tokens.get(peekPoint);
            }
            return null;
        }

    }
}
