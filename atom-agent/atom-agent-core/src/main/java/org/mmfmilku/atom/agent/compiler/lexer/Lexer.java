package org.mmfmilku.atom.agent.compiler.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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

    // 标识符首字母
    private static Pattern letterLine = Pattern.compile("[A-Za-z_\\$@]");
    
    // 标识符字母体
    private static Pattern words = Pattern.compile("[\\w\\$\\.@]");
    
    // 符号
    private static Pattern symbol = Pattern.compile("[\\+\\-\\*/&\\|!\\^=<>;:,\\.\\[\\]%`~\\?]");
    
    // 换行符 通过系统属性获取？ line.separator
    private static Pattern lineSymbol = Pattern.compile("[\\r\\n]");


    private List<Token> tokens;
    
    private String input;

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void execute() {
        LexerHandle handle = new LexerHandle();
        handle.execute();
    }
    
    private class LexerHandle {
        char[] chars;
        int curr = 0;
        int peekPoint = 0;
        char dealChar;

        private LexerHandle() {
            this.chars = input.toCharArray();
            tokens = new ArrayList<>();
        }
        
        private void execute() {
            while (curr < chars.length) {
                dealChar = chars[curr];
                if (match(whiteSpace, dealChar)) {
                    // 跳过空白字符
                    curr++;
                    continue;
                }

                if (match(number, dealChar)) {
                    // 数字
                    String value = readConsecutive(number);
                    tokens.add(new Token(TokenType.Number, value));
                    continue;
                }

                if (match(letterLine, dealChar)) {
                    // 单词句子
                    String value = String.valueOf(dealChar);
                    curr++;
                    value += readConsecutive(words);
                    tokens.add(new Token(TokenType.Words, value));
                    continue;
                }

                if (dealChar == '(') {
                    // 括号
                    tokens.add(new Token(TokenType.LParen, "("));
                    curr++;
                    continue;
                }

                if (dealChar == ')') {
                    // 括号
                    tokens.add(new Token(TokenType.RParen, ")"));
                    curr++;
                    continue;
                }

                if (dealChar == '{') {
                    // 大括号
                    tokens.add(new Token(TokenType.LBrace, "{"));
                    curr++;
                    continue;
                }

                if (dealChar == '}') {
                    // 大括号
                    tokens.add(new Token(TokenType.RBrace, "}"));
                    curr++;
                    continue;
                }
                
                if (dealChar == '\"') {
                    // 字符串
                    String value = String.valueOf(dealChar);
                    curr++;
                    value += readMatchEnd('\"');
                    tokens.add(new Token(TokenType.String, value));
                    continue;
                }

                if (dealChar == '\'') {
                    // 字符串
                    String value = String.valueOf(dealChar);
                    curr++;
                    value += readMatchEnd('\'');
                    tokens.add(new Token(TokenType.Character, value));
                    continue;
                }

                if (match(symbol, dealChar)) {
                    if (dealChar == '/') {
                        char next = peekNext();
                        if (next == '*') {
                            // 多行注释 -> /*内容...*/
                            String value = "/*";
                            curr += 2;
                            value += readMatchEnd("*/");
                            tokens.add(new Token(TokenType.BlockComment, value));
                            continue;
                        } else if (next == '/') {
                            // 单行注释 -> //内容...[换行符]
                            String value = "//";
                            curr += 2;
                            value += readMatchEnd(lineSymbol);
                            tokens.add(new Token(TokenType.Comment, value));
                            continue;
                        } else {
                            // 符号
                            tokens.add(new Token(TokenType.Symbol, String.valueOf(dealChar)));
                            curr++;
                            continue;
                        }
                    } else {
                        // 符号
                        tokens.add(new Token(TokenType.Symbol, String.valueOf(dealChar)));
                        curr++;
                        continue;
                    }
                }
                
                throw new RuntimeException("非法字符 " + dealChar);
            }
        }

        /**
         * 
         * */
        private char peekNext() {
            int peekPoint = curr + 1;
            if (peekPoint < chars.length) {
                return chars[peekPoint];
            }
            return Character.MIN_VALUE;
        }
        
        /**
         * 获取连续匹配的字符
         * */
        private String readConsecutive(Pattern regex) {
            StringBuilder peek = new StringBuilder();
            while (curr < chars.length) {
                char peekChar = chars[curr];
                if (match(regex, peekChar)) {
                    peek.append(peekChar);
                    curr++;
                } else {
                    break;
                }
            }
            return peek.toString();
        }

        /**
         * 获取直到匹配结束符
         * */
        private String readMatchEnd(String matchStr) {
            StringBuilder peek = new StringBuilder();
            while (curr < this.chars.length) {
                char peekChar = this.chars[curr];
                peek.append(peekChar);
                curr++;
                if (peek.length() >= matchStr.length()
                        && peekChar == matchStr.charAt(matchStr.length() - 1)
                        && peek.toString().endsWith(matchStr)) {
                    break;
                }
            }
            return peek.toString();
        }

        /**
         * 获取直到匹配结束符
         * */
        private String readMatchEnd(char matchChar) {
            return readMatchEnd(ch -> ch == matchChar);
        }

        /**
         * 获取直到匹配结束符
         * */
        private String readMatchEnd(Pattern regex) {
            return readMatchEnd(ch -> match(regex, ch));
        }

        private String readMatchEnd(Predicate<Character> predicate) {
            StringBuilder peek = new StringBuilder();
            while (curr < chars.length) {
                char peekChar = chars[curr];
                peek.append(peekChar);
                curr++;
                if (predicate.test(peekChar)) {
                    break;
                }
            }
            return peek.toString();
        }
    }
    
    private boolean match(Pattern regex, char ch) {
        return regex.matcher(String.valueOf(ch)).matches();
    }

    public String showCode() {
        return showCode(false, false);
    }

    public String showCode(boolean beauty, boolean withComment) {
        StringBuilder result = new StringBuilder();
        for (Token token : tokens) {
            result.append(token.showCode(beauty, withComment));
        }
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Token token : tokens) {
            result.append(token.toString());
        }
        return result.toString();
    }
}
