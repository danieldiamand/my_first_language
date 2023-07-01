package com.myfirstlanguage.mfl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.myfirstlanguage.mfl.lexer.Token;
import com.myfirstlanguage.mfl.paser.AstPrinter;
import com.myfirstlanguage.mfl.paser.Expr;
import com.myfirstlanguage.mfl.paser.Parser;
import com.myfirstlanguage.mfl.lexer.Lexer;

public class Mfl {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        runPrompt();
    }

    //This is how to REPL works
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

    //The heart of our code
    private static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) return;

        System.out.println(new AstPrinter().print(expression));
    }

    static public void error(int line, String message){
        report(line, "", message);
    }

    static public void error(Token token, String message){
        report(token.line, token.lexeme, message);
    }

    private static void report(int line, String identifier, String message){
            hadError = true;
            System.err.println("Error(line:" + line + ") "+ identifier + ", " + message);
    }
}
