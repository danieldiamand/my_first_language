package com.myfirstlanguage.mfl.interpreter;

import java.util.HashMap;
import java.util.Map;

import com.myfirstlanguage.mfl.lexer.Token;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    //for var name =
    void define(String name, Object value) {
        values.put(name, value);
    }

    //for name =
    void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return; 

    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }
}
