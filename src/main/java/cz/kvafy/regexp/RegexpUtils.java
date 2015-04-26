package cz.kvafy.regexp;

import org.antlr.v4.runtime.Token;

class RegexpUtils {
    public static String lineAndColumnString(Token token) {
        return String.format("%d:%d", token.getLine(), token.getStartIndex());
    }
}
