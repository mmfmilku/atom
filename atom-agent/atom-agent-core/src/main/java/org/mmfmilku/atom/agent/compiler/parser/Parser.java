package org.mmfmilku.atom.agent.compiler.parser;

import org.mmfmilku.atom.agent.compiler.lexer.Lexer;
import org.mmfmilku.atom.agent.compiler.lexer.Token;
import org.mmfmilku.atom.agent.compiler.lexer.TokenType;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Class;
import org.mmfmilku.atom.agent.compiler.parser.syntax.JavaFile;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Method;
import org.mmfmilku.atom.agent.compiler.parser.syntax.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    
    public void execute() {
        ParserHandle handle = new ParserHandle();
        handle.execute();
    }

    private class ParserHandle {
        List<Token> tokens;
        JavaFile javaFile;
        int curr = 0;
        int peekPoint = 0;
        Token dealToken;

        private ParserHandle() {
            tokens = lexer.getTokens()
                    .stream()
                    .filter(token -> token.getType() != TokenType.BlockComment && token.getType() != TokenType.Comment)
                    .collect(Collectors.toList());
            javaFile = new JavaFile();
        }

        private void execute() {
            parseProgram();
            while (curr < tokens.size()) {
                dealToken = tokens.get(curr);
                parseProgram();
            }
        }
        
        private void parseProgram() {
            while (curr < tokens.size()) {
                dealToken = tokens.get(curr);
                if (dealToken.getType() ==  TokenType.Words) {
                    String value = dealToken.getValue();
                    if ("import".equals(value)) {
                        Node node = parseImport();
                        continue;
                    }
                    if ("class".equals(value)) {
                        Node node = parseClass();
                        continue;
                    }
                }
                throw new RuntimeException("未知字符 " + dealToken.getValue());
            }
        }

        private Node parseImport() {
            return null;
        }

        private Node parseClass() {
            Class clazz = new Class();

            Token className = needNext(TokenType.Words);
            clazz.setValue(className.getValue());

            needNext(TokenType.LBrace);

            List<Method> methods = new ArrayList<>();
            clazz.setMethods(methods);
            Token token;
            while ((token = readNext()) != null && token.getType() != TokenType.RBrace) {
                // 先解析方法
                methods.add(parseMethod());
                // TODO 解析成构造器
                // TODO 解析成员变量
                // TODO 解析静态代码块
            }
            if (token == null) {
                throw new RuntimeException("缺少" + TokenType.RBrace + "值 " + TokenType.RBrace.getFixValue());
            }
            curr++;
            return clazz;
        }

        private Method parseMethod() {
            Token methodNameToken = tokens.get(curr);
            needNext(TokenType.LParen);
            // TODO 方法参数
            
            needNext(TokenType.RParen);
            needNext(TokenType.LBrace);
            Token token = readNext();
            // TODO 代码体
            needNext(TokenType.RBrace);
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
         * 窥探下一个token，不移动指针
         * */
        private Token peekNext() {
            int peekPoint = curr + 1;
            if (peekPoint < tokens.size()) {
                return tokens.get(peekPoint);
            }
            return null;
        }

        /**
         * 读取下一个token，移动指针
         * */
        private Token readNext() {
            curr++;
            if (curr < tokens.size()) {
                return tokens.get(curr);
            }
            return null;
        }

        /**
         * 需要的下一个token，移动指针，判断类型
         * */
        private Token needNext(TokenType type) {
            Token token = readNext();
            if (token == null) {
                throw new RuntimeException("缺少" + type + "值 " + type.getFixValue());
            }
            if (token.getType() != type) {
                throw new RuntimeException("缺少" + type + "值 " + type.getFixValue() + " 输入" + token.getType() + "值 " + token.getValue());
            }
            return token;
        }

        /**
         * 需要的下一个token，移动指针，判断类型
         * */
        private Token needNext(TokenType type, String value) {
            Token token = readNext();
            if (token == null) {
                throw new RuntimeException("缺少" + type + "值 " + value);
            }
            if (token.getType() != type) {
                throw new RuntimeException("缺少" + type + "值 " + value + " 输入" + token.getType() + "值 " + token.getValue());
            }
            return token;
        }

    }
}
