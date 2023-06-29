package com.myfirstlanguage.mfl.lexer;

public class Token {
    final TokenType type; // type
    final String lexeme; // how its written in text
    final Object value; // value stored

    Token(TokenType type, String lexeme, Object value) {
        this.type = type;
        this.lexeme = lexeme;
        this.value = value;
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
