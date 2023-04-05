package main.java.rosenhristov.interpreter;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;
import static main.java.rosenhristov.interpreter.Constants.NEW_LINE;

public class LexingResult {

    private List<Token> tokens;

    private Errors errors;

    public LexingResult(String sourceCode) {
        this.tokens = new LinkedList<>();
        this.errors = new Errors();
    }

    public List<Token> getTokens() {
        if(isNull(tokens)){
            tokens = new LinkedList<>();
        }
        return tokens;
    }

    public Errors getErrors() {
        if(isNull(errors)){
            errors = new Errors();
        }
        return errors;
    }

    public boolean addError(String error) {
        return this.getErrors().addError(error);
    }

    public boolean addToken(Token token) {
        return this.getTokens().add(token);
    }

    public boolean hasErrors() {
        return getErrors().exist();
    }

    public void printErrors() {
        errors.getErrors().forEach(error -> System.out.println("[ERROR]: " + error));
    }

    public void printTokens() {
        getTokens().stream().forEach(token ->
                System.out.println(String.format("[%s]: %s",
                        token.getTypeName().toLowerCase(),
                        token.getToken().equals(NEW_LINE)
                                ? token.getType().toString()
                                : token.getToken())));
    }

    public LineTokensMap buildLineTokensMap() {
        LineTokensMap linesMap = new LineTokensMap();
        int lineNumber = 1;
        List<Token> lineTokens = new LinkedList<>();
        for (Token token : tokens) {
            if (!token.getType().getValue().equals("\\n")) {
                token.setLine(lineNumber);
                lineTokens.add(token);
            } else {
                linesMap.put(lineNumber, lineTokens);
                lineTokens = new LinkedList<>();
                lineNumber++;
            }
        }
        return linesMap;
    }
}

