package main.java.rosenhristov.interpreter;

public class Token {

    private TokenType type;
    private String token;
    private int index;

    public Token(TokenType type, String token, int index) {
        this.type = type;
        this.token = token;
        this.index = index;
    }

    public Token(TokenType type, char token, int index) {
        this(type, String.valueOf(token), index);
    }

    public TokenType getType() {
        return type;
    }

    public String getTypeName() {
        return type.name();
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Token{"
                + type +
                " : " + token
                + " : " + index
                + "}";
    }
}
