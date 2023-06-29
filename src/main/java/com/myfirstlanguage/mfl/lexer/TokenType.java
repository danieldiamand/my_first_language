package com.myfirstlanguage.mfl.lexer;

enum TokenType {
    // Single-character tokens.
  LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE, //brace is a curly bracket
  COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

  // One or two character tokens.
  NOT, NOT_EQUAL, //not refers to '!'
  EQUAL, EQUAL_EQUAL,
  GREATER, GREATER_EQUAL,
  LESS, LESS_EQUAL,

  // Literals.
  IDENTIFIER, STRING, NUMBER,

  // Keywords.
  AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
  PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

  EOF
}