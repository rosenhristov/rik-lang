package main.java.rosenhristov.interpreter;

public class Token {

    private TokenType type;

    /**
     * @token - The value of the token
     */
    private String token;
    private int line;

    private int index;

    private Integer endIndex;

    private boolean isMultiLine;

    public Token(TokenType type, String token, int index) {
        this.type = type;
        this.token = token;
        this.index = index;
    }

    public Token(TokenType type, String token, int index, Integer endIndex) {
        this.type = type;
        this.token = token;
        this.index = index;
        this.endIndex = endIndex;
        if (endIndex != null && endIndex > index) {
            isMultiLine = true;
        }
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

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return "Token{"
                + type.toString() +
                " : " + token
                + " : " + index
                + "}";
    }
}
