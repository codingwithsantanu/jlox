package lox;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

// REMOVED SHORTHAND: import static lox.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and",    TokenType.AND);
        keywords.put("class",  TokenType.CLASS);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    // Main methods for scanning tokens.
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        /* NOTE: Every iteration of the while-loop
         | scans a token. So for each iteration,
         | we set the start of the next token lexeme
         | to the current index, or the last index + 1.
         | Then we call scanToken() which appends the
         | next token to the tokens array, if there is any.
         | We exit this loop whenever the current index is
         | more or equal to the length of the String source.

         * NOTE: Every String source ends with an EOF token.
         | EOF stands for End of File. It signifies the end
         | of the code, which means there is no char left in
         | String source. It is later used in Parser.
        */

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            // Single-character tokens.
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            
            case '.': addToken(TokenType.DOT); break;
            case ',': addToken(TokenType.COMMA); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            
            case '+': addToken(TokenType.SEMICOLON); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.STAR); break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                    // A comment spans until the end of the line.
                } else {
                    addToken(TokenType.SLASH);
                } break;

            // Ignore whitespaces.
            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                line++;
                break;

            // Multiple-character tokens.
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;

            case '"': string(); break;

            default:
                if (isAlpha(c)) {
                    identifier();
                } else if (isDigit(c)) {
                    number();
                } else {
                    Lox.error(line, "Unexpected character '" + c + "'.");
                } break;
        }
    }


    // Methods for handling literals.
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
            // Consume all characters until ".
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        } // ERROR: Imagine this code snippet: "abc.

        // Consume the closing ".
        advance();

        // Trim the surrounding quotes and append the token.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private void number() {
        while (isDigit(peek()))
            advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // Consume the '.'
            while (isDigit(peek()))
                advance();
        }

        // Finally append the number token.
        addToken(
            TokenType.NUMBER,
            Double.parseDouble(source.substring(start, current))
        );

        // You can also handle Integers and Doubles separately.
    }

    private void identifier() {
        while(isAlphaNumeric(peek()))
            advance();
        // NOTE: We use AlphaNumeric to allow abc_123.

        // Append the token to our belowed tokens array.
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null)
            type = TokenType.IDENTIFIER;
        addToken(type);
    }


    // Helper methods for clarity and modularity.
    private char advance() {
        return source.charAt(current++);
        // This returns the char and increments current afterwards.
    }

    private char peek() {
        if (isAtEnd())
            return '\0'; // Null char signifies EOF.
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }


    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected)
            return false;

        current++; // Skip the character if it matches.
        return true;
    }


    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }
    
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }


    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}