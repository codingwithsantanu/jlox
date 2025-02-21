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
        
        /* NOTE: Here the lexeme stores the part of
         | the source code which is converted this token.
         | The literal stores the value of the Token,
         | if there is any, as in Numbers and Strings.
         | Don't you dare mess them up.

         * NOTE: Python stores the starting and ending
         | position of every token instead of just the
         | line number. It stores the line number too.
         | This helps it to show where in the source code
         | the error came from (Traceback).
         |
         | For Example,
         |   x == a + b
         |     ^^
         | They show the USER where they messed up.
        */
    }
    
    public String toString() {
        return type + " \"" + lexeme + "\" " + literal + " " + line;
        // TODO: Implement better String alignment for this.
    }
}