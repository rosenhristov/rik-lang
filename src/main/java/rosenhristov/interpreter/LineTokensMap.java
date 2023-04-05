package main.java.rosenhristov.interpreter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Objects.isNull;
import static main.java.rosenhristov.interpreter.Constants.NEW_LINE;

public class LineTokensMap extends LinkedHashMap<Integer, List<Token>> {

    private Errors errors;

    public LineTokensMap() {;
        this.errors = new Errors();
    }

    public Collection<List<Token>> getTokens() {
        return this.values();
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

    public boolean addToken(int lineNumber, Token token) {
        return this.getTokensLine(lineNumber).add(token);
    }

    public boolean hasErrors() {
        return getErrors().exist();
    }

    public void printErrors() {
        errors.getErrors().forEach(error -> System.out.println("[ERROR]: " + error));
    }

    public void printTokens() {
        getTokens()
                .forEach(tokensList -> tokensList
                        .forEach(token -> System.out.printf("[%s]: %s\n",
                                token.getTypeName().toLowerCase(),
                                token.getToken().equals(NEW_LINE)
                                        ? token.getType().toString()
                                        : token.getToken())));
    }

    public List<Token> getTokensLine(int lineNumber) {
        return this.get(lineNumber);
    }
}
