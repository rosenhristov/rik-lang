package main.java.rosenhristov.interpreter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;
import static main.java.rosenhristov.interpreter.Constants.NEW_LINE;

public class LexingResult {

    private String sourceCode;

    private List<Token> tokens;

    private Errors errors;

    public LexingResult(String sourceCode) {
        this.sourceCode = sourceCode;
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

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}
