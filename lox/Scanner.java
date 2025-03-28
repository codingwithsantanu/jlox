package lox;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import static lox.TokenType.*; // Shorthand for Token Types.

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    // Main methods for Scanning Tokens.
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            // Single-character tokens.
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;

            case '.': addToken(DOT); break;
            case ',': addToken(COMMA); break;
            case ';': addToken(SEMICOLON); break;
            
            case '+': addToken(PLUS); break;
            case '-': addToken(MINUS); break;
            case '*': addToken(STAR); break;

            // Multiple-character tokens.
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                    // NOTE: A comment goes until it reaches NL ('\n').
                } else {
                    addToken(SLASH);
                } // TODO: Add an else if for multi-line comments.
                break;

            case '!':
                addToken(match('=')? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=')? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=')? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=')? GREATER_EQUAL : GREATER);
                break;

            case '"': string(); break;

            // Bunch of Nonsence (Whitespace).
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character '" + c + "'.");
                }
                break;
            // NOTE: break is unnecessary here. You may remove it.
        }
    }


    // Methods for handling Literals.
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
            // NOTE: A string extends until '"'.
            // You can also implement '' char literals.
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing '"'.
        advance();

        // Trim the surrounding quotes and add the token.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void number() {
        // Consume the integer part.
        while (isDigit(peek()))
            advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the '.'
            advance();

            // Consume the fractional part.
            while (isDigit(peek())) advance();
        }

        addToken(
            NUMBER,
            Double.parseDouble(source.substring(start, current))
        );
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();
        // NOTE: We only call identifier() when isAlpha()
        // returns true, so allowing isAlphaNumeric() will
        // never allow 9students but allows students9.

        String lexeme = source.substring(start, current);
        TokenType type = keywords.get(lexeme);
        if (type == null)
            type = IDENTIFIER;
            // NOTE: Keywords are also identifiers but special.
            // Other lexenes have the type IDENTIFIER.
        addToken(type);
    }


    // Helper methods for better clarity and modularity.
    private boolean isAtEnd() {
        return current >= source.length();
        // NOTE: Unlike the terminology, current actually
        // refers to the index one greater than the last
        // character extracted from source.
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected)
            return false;
        current++; // NOTE: We increment current only if
        return true;    // the character matches the expected.
    }


    private char advance() {
        return source.charAt(current++);
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


    private void addToken(TokenType type) {
        addToken(type, null);
        // NOTE: Most tokens other than literals
        // don't have a literal, i.e value like
        // Numbers, Strings, Booleans, and Null.
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, line));
    }


    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
        // NOTE: This compares the ASCII value of c.
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
        // NOTE: Underscore (_) is not an alphabet, but
        // in order to allow _variableNames, we allow
        // _ to act like an alphabet. This prevents
        // 9students but allows _9students.
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
