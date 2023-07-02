package com.myfirstlanguage.mfl.interpreter;

import java.util.List;

public interface MflCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
