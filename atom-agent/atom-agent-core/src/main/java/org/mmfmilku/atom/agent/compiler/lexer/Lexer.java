package org.mmfmilku.atom.agent.compiler.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Lexer
 *
 * @author chenxp
 * @date 2024/8/6:14:18
 */
public class Lexer {
    
    // 空白字符
    private static Pattern whiteSpace = Pattern.compile("\\s");
    
    // 字母
    private static Pattern letter = Pattern.compile("[A-Za-z]");
    
    // 数字
    private static Pattern number = Pattern.compile("\\d");

    // 字母、下划线
    private static Pattern letterLine = Pattern.compile("[A-Za-z_]");
    
    // 数字、字母、下划线 => 标识符
    private static Pattern words = Pattern.compile("\\w");

    // 数字、字母、下划线、点 =>
    private static Pattern idWords = Pattern.compile("[\\w\\.]");

    // 运算符
    private static Pattern operator = Pattern.compile("[\\+\\-\\*/]");

    // 括号
    private static Pattern paren = Pattern.compile("[\\(\\)]");

    // 大括号
    private static Pattern braces = Pattern.compile("[{}]");

    // 符号
    private static Pattern symbol = Pattern.compile("[;,=\"'\\+\\-\\*/.\\S]");


    List<Token> tokens;
    
    String input;

    public Lexer(String input) {
        this.input = input;
        tokens = new ArrayList<>();
    }

    public void execute() {
        char[] chars = input.toCharArray();
        int curr = 0;
        int peekPoint = 0;
        char currChar;
        while (curr < chars.length) {
            currChar = chars[curr];
            
            if (match(whiteSpace, currChar)) {
                // 跳过空白字符
                curr++;
                continue;
            }
            
            if (match(idWords, currChar)) {
                // 数字字母下划线点
                StringBuilder valueBuilder = new StringBuilder();
                while (match(idWords, currChar)) {
                    valueBuilder.append(currChar);
                    currChar = chars[++curr];
                }
                tokens.add(new Token(TokenType.Words, valueBuilder.toString()));
                continue;
            }

            if (match(symbol, currChar)) {
                StringBuilder valueBuilder = new StringBuilder();
                while (match(symbol, currChar)) {
                    valueBuilder.append(currChar);
                    currChar = chars[++curr];
                }
                tokens.add(new Token(TokenType.Symbol, valueBuilder.toString()));
                continue;
            }

            if (match(paren, currChar)) {
                tokens.add(new Token(TokenType.Paren, String.valueOf(currChar)));
                curr++;
                continue;
            }

            if (match(braces, currChar)) {
                tokens.add(new Token(TokenType.Braces, String.valueOf(currChar)));
                curr++;
                continue;
            }

            throw new RuntimeException("err:" + currChar);
        }
    }
    
    private boolean match(Pattern regex, char ch) {
        return regex.matcher(String.valueOf(ch)).matches();
    }
    
}
