package com.myfirstlanguage.mfl.paser;

import java.util.ArrayList;
import java.util.Arrays;
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
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!atEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    /* HANDLING STATEMENTS */
    private Stmt declaration() {
        try {
            if (advanceIf(TokenType.VAR)) {
                return varDeclaration();
            }
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = advanceIfElseThrow(TokenType.IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (advanceIf(TokenType.EQUAL)) {
            initializer = expression();
        }

        advanceIfElseThrow(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt statement() {
        if (advanceIf(TokenType.IF))
            return ifStatement();
        if (advanceIf(TokenType.PRINT))
            return printStatement();
        if (advanceIf(TokenType.WHILE))
            return whileStatement();
        if (advanceIf(TokenType.WHILE))
            return forStatement();
        if (advanceIf(TokenType.LEFT_BRACE))
            return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt whileStatement() {
        advanceIfElseThrow(TokenType.LEFT_BRACKET, "Expect '(' after 'while'.");
        Expr condition = expression();
        advanceIfElseThrow(TokenType.RIGHT_BRACKET, "Expect ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt forStatement() {
        advanceIfElseThrow(TokenType.LEFT_BRACKET, "Expect '(' after 'for'.");

        Stmt initializer;
        if (advanceIf(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (advanceIf(TokenType.VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!checkIf(TokenType.SEMICOLON)) {
            condition = expression();
        }
        advanceIfElseThrow(TokenType.SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!checkIf(TokenType.RIGHT_BRACKET)) {
            increment = expression();
        }
        advanceIfElseThrow(TokenType.RIGHT_BRACKET, "Expect ')' after for clauses.");

        Stmt body = statement();
        if (increment != null) {
            body = new Stmt.Block(
                    Arrays.asList(
                            body,
                            new Stmt.Expression(increment)));
        }

        if (condition == null)
            condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private Stmt ifStatement() {
        advanceIfElseThrow(TokenType.LEFT_BRACKET, "Expect '(' after 'if'.");
        Expr condition = expression();
        advanceIfElseThrow(TokenType.RIGHT_BRACKET, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (advanceIf(TokenType.ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        Expr value = expression();
        advanceIfElseThrow(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        advanceIfElseThrow(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    /* HANDLING EXPRESSIONS */

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!checkIf(TokenType.RIGHT_BRACE) && !atEnd()) {
            statements.add(declaration());
        }

        advanceIfElseThrow(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = or();

        if (advanceIf(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (advanceIf(TokenType.OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (advanceIf(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
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

        if (advanceIf(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
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

    // called match
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

    // When we find something we're not expecting we use synchronize to get us back
    // in place and hopefully find more errors.
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
                default: // TODO: default here?
            }

            advance();
        }
    }

    private static class ParseError extends RuntimeException {
    }

    // called consume
    private Token advanceIfElseThrow(TokenType type, String message) {
        if (checkIf(type))
            return advance();
        else {
            throw error(peek(), message);
        }
    }

    private ParseError error(Token token, String message) {
        Mfl.error(token, message);
        return new ParseError();
    }

}
