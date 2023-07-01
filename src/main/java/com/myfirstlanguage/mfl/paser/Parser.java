package com.myfirstlanguage.mfl.paser;

import java.util.List;

import com.myfirstlanguage.mfl.Mfl;
import com.myfirstlanguage.mfl.lexer.Token;
import com.myfirstlanguage.mfl.lexer.TokenType;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /* PARSING EXPRESSIONS: */
    // main functions:

    /*
     * We create a sort of list of operators where the expressions between call down
     * the functions.
     */
    public Expr parse() {
        try {
        return expression();
        } catch (ParseError error) {
        return null;
        }
    }


    private Expr expression() {
        return equality();
    }

    // == !=
    private Expr equality() {
        Expr expr = comparison();

        while (advanceIf(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUAL)) {
            expr = new Expr.Binary(expr, previous(), comparison());
        }
        return expr;
    }

    // > >= < <=
    private Expr comparison() {
        Expr expr = term();

        while (advanceIf(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            expr = new Expr.Binary(expr, previous(), term());
        }
        return expr;
    }

    // + -
    private Expr term() {
        Expr expr = factor();

        while (advanceIf(TokenType.PLUS, TokenType.MINUS)) {
            expr = new Expr.Binary(expr, previous(), factor());
        }
        return expr;
    }

    // * /
    private Expr factor() {
        Expr expr = unary();

        while (advanceIf(TokenType.STAR, TokenType.SLASH)) {
            expr = new Expr.Binary(expr, previous(), unary());
        }
        return expr;
    }

    // ! -
    private Expr unary() {
        if (advanceIf(TokenType.NOT, TokenType.MINUS)) {
            return new Expr.Unary(previous(), unary());
        }
        return primary();
    }

    private Expr primary() {
        if (advanceIf(TokenType.FALSE))
            return new Expr.Literal(false);
        if (advanceIf(TokenType.TRUE))
            return new Expr.Literal(true);
        if (advanceIf(TokenType.NIL))
            return new Expr.Literal(null);

        if (advanceIf(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().value);
        }

        if (advanceIf(TokenType.LEFT_BRACKET)) {
            Expr expr = expression();
            advanceIfElseThrow(TokenType.RIGHT_BRACKET, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }
 
    // helper functions:
    private Token peek() {
        return tokens.get(current);
    }

    private boolean atEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token advance() {
        if (!atEnd())
            current++;
        return previous();
    }

    private boolean checkIf(TokenType type) {
        if (atEnd())
            return false;
        return peek().type == type;
    }

    private boolean advanceIf(TokenType... types) {
        for (TokenType type : types) {
            if (checkIf(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    // error management:
    
    //When we find something we're not expecting we use synchronize to get us back in place and hopefully find more errors.
    private void synchronize() {
        advance();

        while (!atEnd()) {
            if (previous().type == TokenType.SEMICOLON)
                return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
                default: //TODO: default here?
            }

            advance();
        }
    }

    private static class ParseError extends RuntimeException {
    }

    private Token advanceIfElseThrow(TokenType type, String message) {
        if (checkIf(type))
            return advance();
        else{
            throw error(peek(), message);
        }
    }

    private ParseError error(Token token, String message) {
        Mfl.error(token, message);
        return new ParseError();
    }

}
