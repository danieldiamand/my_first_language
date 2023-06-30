package com.myfirstlanguage.mfl.paser;

import com.myfirstlanguage.mfl.lexer.Token;

abstract class Expr {
    interface ExprVisitor<R> {
        R visit(Binary expr);
        R visit(Unary expr);
        R visit(Literal expr);
        R visit(Variable expr);
    }

    abstract <R> R accept(ExprVisitor<R> visitor);

    static class Binary extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    static class Unary extends Expr{
        final Expr expression;
        final Token operator;
        
        Unary(Expr expression, Token operator){
            this.expression = expression;
            this.operator = operator;
        }

        @Override
        <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    static class Literal extends Expr {
        final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    static class Variable extends Expr {
        final Token name;

        Variable(Token name) {
             this.name = name;
        }

        @Override
        <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visit(this);
        }
    }
}
