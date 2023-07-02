package com.myfirstlanguage.mfl.interpreter;

import com.myfirstlanguage.mfl.lexer.Token;

public class RuntimeError extends RuntimeException {
    public final Token token;

    RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}