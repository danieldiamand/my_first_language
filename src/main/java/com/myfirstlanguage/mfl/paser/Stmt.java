package com.myfirstlanguage.mfl.paser;

import java.beans.Expression;
import java.util.List;
import java.util.function.Function;

import com.myfirstlanguage.mfl.lexer.Token;

public abstract class Stmt {
    public interface StmtVisitor<R> {
        R visit(Expression stmt);

        R visit(Print stmt);

        R visit(Var stmt);

        R visit(Block stmt);

        R visit(If stmt);

        R visit(While stmt);
    }

    public abstract <R> R accept(StmtVisitor<R> visitor);

    public static class While extends Stmt {
        public final Expr condition;
        public final Stmt body;

        While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    public static class If extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch;

        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor) {
            return visitor.visit(this);
        }

    }

    public static class Block extends Stmt {
        public final List<Stmt> statements;

        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Expression extends Stmt {
        public final Expr expression;

        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Print extends Stmt {
        public final Expr expression;

        Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }

    public static class Var extends Stmt {
        public final Token name;
        public final Expr initializer;

        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        public <R> R accept(StmtVisitor<R> visitor) {
            return visitor.visit(this);
        }
    }
}
