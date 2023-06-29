package com.myfirstlanguage.mfl.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myfirstlanguage.mfl.Mfl;
import com.myfirstlanguage.mfl.lexer.TokenType.*;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0; // points to first character in lexeme being scanned
    private int current = 0; // points to character currently looked at
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!atEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null));
        return tokens;
    }
    

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_BRACKET); break;
            case ')': addToken(TokenType.RIGHT_BRACKET); break;   
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break; 
            case '-': addToken(TokenType.MINUS); break;       
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case ' ':
            case '\r':
            case '\t': break;// Ignore whitespace.
            case '\n': line++; break; 
            case '!':
                addToken(advanceIf('=') ? TokenType.NOT_EQUAL : TokenType.NOT);
                break;
            case '=':
                addToken(advanceIf('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(advanceIf('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(advanceIf('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '/': // could be a comment!
                if (advanceIf('/')) { inlineComment();   
                } else if (advanceIf('*')) { blockComment();
                } else { addToken(TokenType.SLASH); }
                break;
            case '"': string(); break;
            default:
                if (isDigit(c)) { number();
                } else if (isAlpha(c)) { identifier();
                } else { Mfl.error("Unexpected character."); }  
                break;
        }
    }

    /* SPECIAL TOKEN FUNCTIONS: */
    private void string() {
        while (peek() != '"' && !atEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (atEnd()) {
            Mfl.error("Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private void number() {
        while (isDigit(peek()))
            advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    // identifier is for keywords like and, class, for etc
    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null){
            addToken(TokenType.IDENTIFIER);
        } else{
            addToken(type);
        }
    }

    private void inlineComment() {
        // A comment goes until the end of the line.
        while (peek() != '\n' && !atEnd()) {
            advance();
        }
    }
    
    private void blockComment() {
        int nestCount = 1;
        while (nestCount > 0 && !atEnd()) {
            //System.out.println("char ="+peek()+", nestCount ="+nestCount);
            if (peek() == '/' && peekNext() == '*'){
                nestCount += 1;
            } else if (peek() == '*' && peekNext() == '/'){
                nestCount -= 1;
            } else if (peek() == '\n'){
                line++;
            }
            advance();
        }
        
        if (atEnd()) {
            Mfl.error("Unterminated comment.");
            return;
        }

        //get of out of comment
        advance();
    }


    /* LITTLE HELPER FUNCTIONS: */
    // navigation:
    private boolean atEnd() {
        return current >= source.length();
    }

    private boolean atEnd(int pos) {
        return pos >= source.length();
    }

    private char peek() {
        if (atEnd())
            return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (atEnd(current + 1))
            return '\0';
        return source.charAt(current + 1);
    }

    private char advance() {
        char val = peek();
        current++;
        return val;
    }

    private boolean advanceIf(char expected) {
        if (peek() != expected) {
            return false;
        } else {
            advance();
            return true;
        }
    }

    // adding tokens:
    private void addToken(TokenType type, Object value) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, value));
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // char checks:
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
