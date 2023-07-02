package com.myfirstlanguage.mfl.paser;

import java.util.List;

import com.myfirstlanguage.mfl.lexer.Token;

public abstract class Expr {
    public interface ExprVisitor<R> {
        R visit(Binary expr);

        R visit(Unary expr);

        R visit(Grouping expr);

        R visit(Literal expr);

        R visit(Variable expr);

        R visit(Assign expr);

        R visit(Logical expr);

        R visit(Call expr);
    }

    public abstract <R> R accept(ExprVisitor<R> visitor);

    public static class Call extends Expr {
        public final Expr callee;
        public final Token paren;
        public final List<Expr> arguments;

        Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }

        public final Expr left;
        public final Token operator;
        public final Expr right;
    }

    public static class Binary extends Expr {
        public final Expr left;
        public final Token operator;
        public final Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Unary extends Expr {
        public final Token operator;
        public final Expr expression;

        public Unary(Token operator, Expr expression) {
            this.operator = operator;
            this.expression = expression;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Grouping extends Expr {
        public final Expr expression;

        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Literal extends Expr {
        public final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Variable extends Expr {
        public final Token name;

        Variable(Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Assign extends Expr {
        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public <R> R accept(ExprVisitor<R> visitor) {
            return visitor.visit(this);
        }

        public final Token name;
        public final Expr value;
    }
}
