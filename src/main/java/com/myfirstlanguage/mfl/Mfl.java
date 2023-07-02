package com.myfirstlanguage.mfl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.myfirstlanguage.mfl.lexer.Token;
import com.myfirstlanguage.mfl.paser.Expr;
import com.myfirstlanguage.mfl.paser.Parser;
import com.myfirstlanguage.mfl.paser.Stmt;
import com.myfirstlanguage.mfl.lexer.Lexer;
import com.myfirstlanguage.mfl.interpreter.Interpreter;
import com.myfirstlanguage.mfl.interpreter.RuntimeError;

public class Mfl {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64); // [64]
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError)
            System.exit(65);
        if (hadRuntimeError)
            System.exit(70);
    }

    // This is how to REPL works
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null)
                break;
            run(line);
            hadError = false;
        }
    }

    // The heart of our code
    private static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error.
        if (hadError)
            return;

        interpreter.interpret(statements);
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    static public void error(int line, String message) {
        report(line, "", message);
    }

    static public void error(Token token, String message) {
        report(token.line, token.lexeme, message);
    }

    private static void report(int line, String identifier, String message) {
        hadError = true;
        System.err.println("Error(line:" + line + ") " + identifier + ", " + message);
    }
}
