package com.myfirstlanguage.mfl.lexer;

public class Token {
    public final TokenType type; // type
    public final String lexeme; // how its written in text
    public final Object value; // value stored
    public final int line; //line in code the Token is from

    Token(TokenType type, String lexeme, Object value, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.value = value;
        this.line = line;
    }

    @Override
    public String toString() {
        if (value == null) {
            return type + "";
        } else {
            return type + " " + value;
        }
    }
}
