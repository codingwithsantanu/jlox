package lox;

class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        String lexeme = this.lexeme.isEmpty()? "''" : this.lexeme;
        return type + " " + lexeme + " " + literal + " " + line;
        // TODO: Upgrade the __repr__() format.
    }
}
