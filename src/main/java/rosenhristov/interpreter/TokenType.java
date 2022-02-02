package main.java.rosenhristov.interpreter;

import java.util.regex.Pattern;

public enum TokenType {
    KEYWORD("([a-zа-я0-9])+"),
    IDENTIFIER("([a-zа-яA-ZА-Я0-9_])+"),
    OPERATOR("[\\+\\-\\\\*/=%&\\|]{1,2}"),
    NUMBER("(\\d)+"),
    CHAR_LITERAL("([\\'])+"),
    STRING_LITERAL("([\"])+"),
    SEPARATOR("([\\{\\}\\[\\]\\(\\)\\.,:;\"\'])+"),
    COMMENT("([/\\*]){1,2}"),
    DOC("/**"),
    NEWLINE("\\n"),
    WHITESPACE("[\\s|\\t|\\r|\\f|\\n]+"),
    EOF("EOF");

    private String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Pattern getPattern() {
        return Pattern.compile(value);
    }

    public boolean matches(char ch) {
        return matches(String.valueOf(ch));
    }

    public boolean matches(String word) {
        return getPattern().matcher(word).matches();
    }

    public static boolean isTokenType(TokenType tokenType, char c) {
        return tokenType == DOC
                ? tokenType.getValue().equals(c)
                : tokenType.matches(c);
    }

    @Override
    public String toString() {
        return this.name();
    }
}
